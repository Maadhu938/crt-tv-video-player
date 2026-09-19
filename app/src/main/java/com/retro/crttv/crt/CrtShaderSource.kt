package com.retro.crttv.crt

import org.intellij.lang.annotations.Language

object CrtShaderSource {
    @Language("AGSL")
    const val AGSL_CRT_SHADER = """
        uniform shader composable;
        uniform float2 uResolution;
        uniform float uTime;
        uniform float uScanlines;
        uniform float uCurvature;
        uniform float uNoise;
        uniform float uColor;
        uniform float uBrightness;
        uniform float uContrast;
        uniform float uRgbSeparation;
        uniform float uFlicker;
        uniform float uVignette;

        half4 main(float2 fragCoord) {
            float2 uv = fragCoord / uResolution;
            
            // 1. CRT Barrel Curvature
            float2 centered = uv * 2.0 - 1.0;
            float r2 = dot(centered, centered);
            float2 warped = centered * (1.0 + uCurvature * 0.22 * r2);
            float2 curvedUv = (warped + 1.0) * 0.5;

            // Outside CRT tube boundary -> deep black border
            if (curvedUv.x < 0.0 || curvedUv.x > 1.0 || curvedUv.y < 0.0 || curvedUv.y > 1.0) {
                return half4(0.0, 0.0, 0.0, 1.0);
            }

            // 2. RGB Separation / Chromatic Aberration
            float sep = (uRgbSeparation * 0.005) * (1.0 + r2 * 0.8);
            float r = composable.eval(float2(curvedUv.x + sep, curvedUv.y) * uResolution).r;
            float g = composable.eval(curvedUv * uResolution).g;
            float b = composable.eval(float2(curvedUv.x - sep, curvedUv.y) * uResolution).b;
            float a = composable.eval(curvedUv * uResolution).a;
            half3 col = half3(r, g, b);

            // 3. Scanlines
            float scan = sin(curvedUv.y * uResolution.y * 1.57) * 0.5 + 0.5;
            col *= (1.0 - uScanlines * 0.42 * (1.0 - scan));

            // 4. Subtle RGB subpixel grille
            float subpixel = mod(fragCoord.x, 3.0);
            if (subpixel < 1.0) {
                col.r *= 1.06;
                col.b *= 0.96;
            } else if (subpixel < 2.0) {
                col.g *= 1.06;
            } else {
                col.b *= 1.06;
                col.r *= 0.96;
            }

            // 5. CRT Flicker & Beam Pulse
            float flickerMod = 1.0 + (sin(uTime * 60.0) * 0.015 + sin(uTime * 17.0) * 0.02) * uFlicker;
            col *= flickerMod;

            // 6. Analog Noise & Jitter
            float noiseVal = fract(sin(dot(curvedUv + float2(uTime * 0.07, uTime * 0.03), float2(12.9898, 78.233))) * 43758.5453);
            col += (noiseVal - 0.5) * (uNoise * 0.16);

            // 7. Vignette
            float vig = 1.0 - dot(centered * 0.55, centered * 0.55) * (uVignette * 1.8);
            col *= clamp(vig, 0.0, 1.0);

            // 8. Brightness, Contrast & Saturation
            col = (col - 0.5) * uContrast + 0.5;
            col *= uBrightness;

            float luma = dot(col, half3(0.299, 0.587, 0.114));
            col = mix(half3(luma), col, uColor);

            return half4(clamp(col, 0.0, 1.0), a);
        }
    """
}
