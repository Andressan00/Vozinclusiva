package com.example.vozinclusiva.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** Lee texto en voz alta usando el motor de voz (Text-to-Speech) de Android. */
class SpeechController(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pending: String? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val engine = tts ?: return
        var result = engine.setLanguage(Locale("es", "CO"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            result = engine.setLanguage(Locale("es"))
        }
        ready = result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED
        if (ready) pending?.let { speak(it) }
        pending = null
    }

    fun speak(text: String) {
        if (text.isBlank()) return
        if (!ready) { pending = text; return }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "vozinclusiva_utterance")
    }

    fun stop() { tts?.stop() }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}

@Composable
fun rememberSpeechController(): SpeechController {
    val context = LocalContext.current
    val controller = remember { SpeechController(context) }
    DisposableEffect(Unit) { onDispose { controller.shutdown() } }
    return controller
}