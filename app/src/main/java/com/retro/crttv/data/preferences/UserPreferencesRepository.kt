package com.retro.crttv.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.retro.crttv.crt.AspectRatioMode
import com.retro.crttv.crt.CrtPreset
import com.retro.crttv.crt.CrtSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "crt_tv_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val PRESET_ID = stringPreferencesKey("preset_id")
        val SCANLINES = floatPreferencesKey("scanlines")
        val CURVATURE = floatPreferencesKey("curvature")
        val NOISE = floatPreferencesKey("noise")
        val COLOR = floatPreferencesKey("color")
        val BRIGHTNESS = floatPreferencesKey("brightness")
        val CONTRAST = floatPreferencesKey("contrast")
        val RGB_SEPARATION = floatPreferencesKey("rgb_separation")
        val FLICKER = floatPreferencesKey("flicker")
        val VIGNETTE = floatPreferencesKey("vignette")
        val SHOW_TIMESTAMP = booleanPreferencesKey("show_timestamp")
        val ASPECT_RATIO = stringPreferencesKey("aspect_ratio")
        val EFFECTS_ENABLED = booleanPreferencesKey("effects_enabled")
        val CHANNEL = intPreferencesKey("channel")
        val VOLUME = intPreferencesKey("volume")
    }

    val crtSettingsFlow: Flow<CrtSettings> = context.dataStore.data.map { prefs ->
        val defaultClassic = CrtPreset.CLASSIC.defaultSettings
        val aspectString = prefs[Keys.ASPECT_RATIO] ?: AspectRatioMode.FOUR_BY_THREE.name
        val aspect = try {
            AspectRatioMode.valueOf(aspectString)
        } catch (e: Exception) {
            AspectRatioMode.FOUR_BY_THREE
        }

        CrtSettings(
            scanlines = prefs[Keys.SCANLINES] ?: defaultClassic.scanlines,
            curvature = prefs[Keys.CURVATURE] ?: defaultClassic.curvature,
            noise = prefs[Keys.NOISE] ?: defaultClassic.noise,
            color = prefs[Keys.COLOR] ?: defaultClassic.color,
            brightness = prefs[Keys.BRIGHTNESS] ?: defaultClassic.brightness,
            contrast = prefs[Keys.CONTRAST] ?: defaultClassic.contrast,
            rgbSeparation = prefs[Keys.RGB_SEPARATION] ?: defaultClassic.rgbSeparation,
            flicker = prefs[Keys.FLICKER] ?: defaultClassic.flicker,
            vignette = prefs[Keys.VIGNETTE] ?: defaultClassic.vignette,
            showTimestamp = prefs[Keys.SHOW_TIMESTAMP] ?: true,
            aspectRatio = aspect,
            effectsEnabled = prefs[Keys.EFFECTS_ENABLED] ?: true
        )
    }

    val activePresetFlow: Flow<CrtPreset> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.PRESET_ID] ?: CrtPreset.CLASSIC.id
        CrtPreset.fromId(id)
    }

    val channelFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.CHANNEL] ?: 3
    }

    val volumeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.VOLUME] ?: 24
    }

    suspend fun saveCrtSettings(settings: CrtSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCANLINES] = settings.scanlines
            prefs[Keys.CURVATURE] = settings.curvature
            prefs[Keys.NOISE] = settings.noise
            prefs[Keys.COLOR] = settings.color
            prefs[Keys.BRIGHTNESS] = settings.brightness
            prefs[Keys.CONTRAST] = settings.contrast
            prefs[Keys.RGB_SEPARATION] = settings.rgbSeparation
            prefs[Keys.FLICKER] = settings.flicker
            prefs[Keys.VIGNETTE] = settings.vignette
            prefs[Keys.SHOW_TIMESTAMP] = settings.showTimestamp
            prefs[Keys.ASPECT_RATIO] = settings.aspectRatio.name
            prefs[Keys.EFFECTS_ENABLED] = settings.effectsEnabled
        }
    }

    suspend fun applyPreset(preset: CrtPreset) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PRESET_ID] = preset.id
            val s = preset.defaultSettings
            prefs[Keys.SCANLINES] = s.scanlines
            prefs[Keys.CURVATURE] = s.curvature
            prefs[Keys.NOISE] = s.noise
            prefs[Keys.COLOR] = s.color
            prefs[Keys.BRIGHTNESS] = s.brightness
            prefs[Keys.CONTRAST] = s.contrast
            prefs[Keys.RGB_SEPARATION] = s.rgbSeparation
            prefs[Keys.FLICKER] = s.flicker
            prefs[Keys.VIGNETTE] = s.vignette
        }
    }

    suspend fun resetToDefault() {
        applyPreset(CrtPreset.CLASSIC)
    }

    suspend fun updateChannel(channel: Int) {
        val clamped = channel.coerceIn(1, 99)
        context.dataStore.edit { it[Keys.CHANNEL] = clamped }
    }

    suspend fun updateVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        context.dataStore.edit { it[Keys.VOLUME] = clamped }
    }
}
