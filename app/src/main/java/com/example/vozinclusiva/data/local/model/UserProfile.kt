package com.example.vozinclusiva.data.local.model

enum class UserType(val label: String) {
    SORDA("Persona sorda"),
    OYENTE("Persona oyente")
}

/** Datos básicos del usuario que se guardan en el dispositivo. */
data class UserProfile(
    val name: String = "",
    val userType: UserType = UserType.SORDA,
    val age: String = "",
    val phone: String = "",
    val emergencyName: String = "",
    val emergencyPhone: String = "",
    val notes: String = "" // alergias, EPS, condiciones médicas, etc.
) {
    val isEmpty: Boolean get() = name.isBlank()

    /** Texto que se muestra y que el sintetizador de voz lee en voz alta. */
    fun toReadableText(): String = buildString {
        append("Mi nombre es $name. ")
        append("Soy ${userType.label.lowercase()}. ")
        if (age.isNotBlank()) append("Tengo $age años. ")
        if (phone.isNotBlank()) append("Mi teléfono es $phone. ")
        if (emergencyName.isNotBlank() || emergencyPhone.isNotBlank()) {
            append("Mi contacto de emergencia es ")
            append(emergencyName.ifBlank { "una persona de confianza" })
            if (emergencyPhone.isNotBlank()) append(", teléfono $emergencyPhone")
            append(". ")
        }
        if (notes.isNotBlank()) append("Información importante: $notes.")
    }.trim()
}