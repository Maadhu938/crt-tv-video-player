package com.retro.crttv.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * High-performance Chromeless YouTube Player integrated inside the CRT TV Cathode Tube.
 * Plays all YouTube videos, shorts, and livestreams reliably with no scraper downtime.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CrtYouTubeView(
    videoId: String,
    isPlaying: Boolean,
    isPoweredOn: Boolean,
    volume: Float,
    seekPositionMs: Long,
    onPlaybackUpdated: (isPlaying: Boolean, currentMs: Long, durationMs: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var lastSeekTarget by remember { mutableStateOf(-1L) }

    // React to Play / Pause state
    LaunchedEffect(isPlaying, isPoweredOn) {
        if (!isPoweredOn || !isPlaying) {
            webViewInstance?.evaluateJavascript("pauseVideo();", null)
        } else {
            webViewInstance?.evaluateJavascript("playVideo();", null)
        }
    }

    // React to Volume changes
    LaunchedEffect(volume) {
        val volInt = (volume * 100).toInt().coerceIn(0, 100)
        webViewInstance?.evaluateJavascript("setVolume($volInt);", null)
    }

    // React to Seek / Rewind / Fast-Forward
    LaunchedEffect(seekPositionMs) {
        if (seekPositionMs >= 0 && seekPositionMs != lastSeekTarget) {
            lastSeekTarget = seekPositionMs
            val sec = seekPositionMs / 1000f
            webViewInstance?.evaluateJavascript("seekTo($sec);", null)
        }
    }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(Color.BLACK)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
                }

                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (isPlaying && isPoweredOn) {
                            view?.evaluateJavascript("playVideo();", null)
                        }
                    }
                }

                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun notifyReady() {
                            post {
                                if (isPlaying && isPoweredOn) {
                                    evaluateJavascript("playVideo();", null)
                                }
                            }
                        }

                        @JavascriptInterface
                        fun notifyStateChange(state: Int) {
                            post {
                                // 1 = playing, 2 = paused, 0 = ended, 3 = buffering
                                val playing = (state == 1 || state == 3)
                                onPlaybackUpdated(playing, -1L, -1L)
                            }
                        }

                        @JavascriptInterface
                        fun notifyTimeUpdate(currentSec: Float, durationSec: Float) {
                            post {
                                val curMs = (currentSec * 1000).toLong()
                                val durMs = (durationSec * 1000).toLong()
                                onPlaybackUpdated(isPlaying, curMs, durMs)
                            }
                        }
                    },
                    "AndroidBridge"
                )

                val embedHtml = buildYouTubeHtml(videoId)
                loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                webViewInstance = this
            }
        },
        update = { webView ->
            webViewInstance = webView
        },
        modifier = modifier.fillMaxSize()
    )

    DisposableEffect(videoId) {
        onDispose {
            webViewInstance?.destroy()
            webViewInstance = null
        }
    }
}

private fun buildYouTubeHtml(videoId: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
          <style>
            * { margin:0; padding:0; box-sizing:border-box; background:#000; overflow:hidden; }
            html, body { width:100%; height:100%; background:#000; }
            #player { width:100%; height:100%; position:absolute; top:0; left:0; }
          </style>
        </head>
        <body>
          <div id="player"></div>
          <script>
            var tag = document.createElement('script');
            tag.src = "https://www.youtube.com/iframe_api";
            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

            var player;
            function onYouTubeIframeAPIReady() {
              player = new YT.Player('player', {
                width: '100%',
                height: '100%',
                videoId: '$videoId',
                playerVars: {
                  'autoplay': 1,
                  'playsinline': 1,
                  'controls': 0,
                  'rel': 0,
                  'modestbranding': 1,
                  'fs': 0,
                  'disablekb': 1,
                  'iv_load_policy': 3
                },
                events: {
                  'onReady': onPlayerReady,
                  'onStateChange': onPlayerStateChange
                }
              });
            }
            function onPlayerReady(event) {
              event.target.playVideo();
              if (window.AndroidBridge) {
                window.AndroidBridge.notifyReady();
              }
              setInterval(function() {
                if (player && player.getCurrentTime && player.getDuration) {
                  var c = player.getCurrentTime();
                  var d = player.getDuration();
                  if (window.AndroidBridge && d > 0) {
                    window.AndroidBridge.notifyTimeUpdate(c, d);
                  }
                }
              }, 500);
            }
            function onPlayerStateChange(event) {
              if (window.AndroidBridge) {
                window.AndroidBridge.notifyStateChange(event.data);
              }
            }
            function playVideo() { if (player && player.playVideo) player.playVideo(); }
            function pauseVideo() { if (player && player.pauseVideo) player.pauseVideo(); }
            function seekTo(sec) { if (player && player.seekTo) player.seekTo(sec, true); }
            function setVolume(vol) { if (player && player.setVolume) player.setVolume(vol); }
          </script>
        </body>
        </html>
    """.trimIndent()
}
