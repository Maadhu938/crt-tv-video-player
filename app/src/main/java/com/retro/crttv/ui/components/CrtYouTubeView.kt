package com.retro.crttv.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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
 * Uses YouTube No-Cookie official embed with direct permissions policy for encrypted-media,
 * allowing all monetized and music videos to play without "Video unavailable" errors.
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
    onErrorOccurred: (String) -> Unit = {},
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

                // Accept third-party cookies for YouTube embed authentication
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = WebSettings.LOAD_DEFAULT
                    allowFileAccess = true
                    allowContentAccess = true
                }

                webChromeClient = object : WebChromeClient() {
                    // Critical for DRM / encrypted-media on modern YouTube videos
                    override fun onPermissionRequest(request: PermissionRequest?) {
                        request?.grant(request.resources)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        // Keep playback inside the CRT screen
                        return false
                    }

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

                        @JavascriptInterface
                        fun notifyError(errorCode: Int) {
                            post {
                                val msg = when (errorCode) {
                                    101, 150 -> "EMBED RESTRICTED BY OWNER"
                                    100 -> "VIDEO NOT FOUND"
                                    else -> "YOUTUBE ERROR ($errorCode)"
                                }
                                onErrorOccurred(msg)
                            }
                        }
                    },
                    "AndroidBridge"
                )

                val embedHtml = buildYouTubeHtml(videoId)
                loadDataWithBaseURL("https://www.youtube-nocookie.com", embedHtml, "text/html", "UTF-8", null)
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
            #player-wrap { width:100%; height:100%; position:absolute; top:0; left:0; }
            iframe { width:100%; height:100%; border:none; }
          </style>
        </head>
        <body>
          <div id="player-wrap">
            <iframe id="player"
              type="text/html"
              src="https://www.youtube-nocookie.com/embed/$videoId?enablejsapi=1&autoplay=1&playsinline=1&controls=0&rel=0&modestbranding=1&fs=0&iv_load_policy=3&origin=https://www.youtube-nocookie.com&widget_referrer=https://www.youtube-nocookie.com"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
              allowfullscreen
              frameborder="0">
            </iframe>
          </div>

          <script>
            var tag = document.createElement('script');
            tag.src = "https://www.youtube.com/iframe_api";
            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

            var player;
            function onYouTubeIframeAPIReady() {
              player = new YT.Player('player', {
                events: {
                  'onReady': onPlayerReady,
                  'onStateChange': onPlayerStateChange,
                  'onError': onPlayerError
                }
              });
            }

            function onPlayerReady(event) {
              try { event.target.playVideo(); } catch(e){}
              if (window.AndroidBridge) {
                window.AndroidBridge.notifyReady();
              }
              setInterval(function() {
                try {
                  if (player && player.getCurrentTime && player.getDuration) {
                    var c = player.getCurrentTime();
                    var d = player.getDuration();
                    if (window.AndroidBridge && d > 0) {
                      window.AndroidBridge.notifyTimeUpdate(c, d);
                    }
                  }
                } catch(e){}
              }, 500);
            }

            function onPlayerStateChange(event) {
              if (window.AndroidBridge) {
                window.AndroidBridge.notifyStateChange(event.data);
              }
            }

            function onPlayerError(event) {
              if (window.AndroidBridge) {
                window.AndroidBridge.notifyError(event.data);
              }
            }

            function playVideo() { try { if (player && player.playVideo) player.playVideo(); } catch(e){} }
            function pauseVideo() { try { if (player && player.pauseVideo) player.pauseVideo(); } catch(e){} }
            function seekTo(sec) { try { if (player && player.seekTo) player.seekTo(sec, true); } catch(e){} }
            function setVolume(vol) { try { if (player && player.setVolume) player.setVolume(vol); } catch(e){} }
          </script>
        </body>
        </html>
    """.trimIndent()
}
