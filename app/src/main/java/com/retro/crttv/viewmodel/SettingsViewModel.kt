package com.retro.crttv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retro.crttv.crt.AspectRatioMode
import com.retro.crttv.crt.CrtPreset
import com.retro.crttv.crt.CrtSettings
import com.retro.crttv.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val crtSettings: StateFlow<CrtSettings> = preferencesRepository.crtSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CrtSettings())

    val activePreset: StateFlow<CrtPreset> = preferencesRepository.activePresetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CrtPreset.CLASSIC)

    fun applyPreset(preset: CrtPreset) {
        viewModelScope.launch {
            preferencesRepository.applyPreset(preset)
        }
    }

    fun updateScanlines(value: Float) {
        updateSettings { it.copy(scanlines = value) }
    }

    fun updateCurvature(value: Float) {
        updateSettings { it.copy(curvature = value) }
    }

    fun updateNoise(value: Float) {
        updateSettings { it.copy(noise = value) }
    }

    fun updateColor(value: Float) {
        updateSettings { it.copy(color = value) }
    }

    fun updateBrightness(value: Float) {
        updateSettings { it.copy(brightness = value) }
    }

    fun updateContrast(value: Float) {
        updateSettings { it.copy(contrast = value) }
    }

    fun updateRgbSeparation(value: Float) {
        updateSettings { it.copy(rgbSeparation = value) }
    }

    fun updateFlicker(value: Float) {
        updateSettings { it.copy(flicker = value) }
    }

    fun setShowTimestamp(enabled: Boolean) {
        updateSettings { it.copy(showTimestamp = enabled) }
    }

    fun setAspectRatio(mode: AspectRatioMode) {
        updateSettings { it.copy(aspectRatio = mode) }
    }

    fun setEffectsEnabled(enabled: Boolean) {
        updateSettings { it.copy(effectsEnabled = enabled) }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            preferencesRepository.resetToDefault()
        }
    }

    private fun updateSettings(transform: (CrtSettings) -> CrtSettings) {
        viewModelScope.launch {
            val updated = transform(crtSettings.value)
            preferencesRepository.saveCrtSettings(updated)
        }
    }
}
