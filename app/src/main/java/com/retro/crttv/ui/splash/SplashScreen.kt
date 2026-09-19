package com.retro.crttv.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.retro.crttv.R
import com.retro.crttv.ui.theme.CrtDarkCharcoal
import com.retro.crttv.ui.theme.CrtPhosphorGreen
import com.retro.crttv.ui.theme.CrtTextMuted
import com.retro.crttv.ui.theme.CrtTextWarm
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alphaAnim = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(600, easing = LinearEasing))
        progressAnim.animateTo(1f, animationSpec = tween(1500, easing = LinearEasing))
        delay(300)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Glowing 3D CRT TV Graphic
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = CrtPhosphorGreen)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_retro),
                    contentDescription = "CRT TV Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title "CRT TV"
            Text(
                text = "CRT TV",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CrtTextWarm,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle
            Text(
                text = "Videos feel better\nin the old days.",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = CrtTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Thin Retro Green Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color(0xFF202026))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim.value)
                        .height(3.dp)
                        .background(CrtPhosphorGreen)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Loading memories..."
            Text(
                text = "Loading memories...",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF6B6A64),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
