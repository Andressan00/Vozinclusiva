package com.example.vozinclusiva.data.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class HistoryEntry(val text: String, val timestamp: Long)

/**
 * Historial de mensajes dichos en voz alta, guardado localmente en el dispositivo.
 * El más reciente aparece primero. Guarda como máximo [MAX_ENTRIES] mensajes.
 */
class MessageHistoryRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAll(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                HistoryEntry(obj.getString("text"), obj.getLong("time"))
            }
        }.getOrDefault(emptyList())
    }

    fun add(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val updated = (listOf(HistoryEntry(clean, System.currentTimeMillis())) + getAll())
            .take(MAX_ENTRIES)
        val array = JSONArray()
        updated.forEach {
            array.put(JSONObject().put("text", it.text).put("time", it.timestamp))
        }
        prefs.edit().putString(KEY_ENTRIES, array.toString()).apply()
    }

    fun clear() = prefs.edit().remove(KEY_ENTRIES).apply()

    private companion object {
        const val PREFS_NAME = "vozinclusiva_history"
        const val KEY_ENTRIES = "entries"
        const val MAX_ENTRIES = 100
    }
}