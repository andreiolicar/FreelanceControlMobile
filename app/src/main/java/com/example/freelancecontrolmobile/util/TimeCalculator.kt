package com.example.freelancecontrolmobile.util

/**
 * Utilitário para cálculos de horas e valores trabalhados.
 */
object TimeCalculator {

    /**
     * Calcula a diferença em minutos entre dois horários no formato "HH:mm".
     * Exemplo: "08:30" e "12:00" retorna 210.
     * @throws IllegalArgumentException se o horário final for menor ou igual ao inicial.
     */
    fun calculateDurationMinutes(startTime: String, endTime: String): Int {
        val startParts = startTime.split(":").map { it.toInt() }
        val endParts = endTime.split(":").map { it.toInt() }

        val startTotalMinutes = startParts[0] * 60 + startParts[1]
        val endTotalMinutes = endParts[0] * 60 + endParts[1]

        val duration = endTotalMinutes - startTotalMinutes

        if (duration <= 0) {
            throw IllegalArgumentException("O horário final deve ser maior que o horário inicial.")
        }

        return duration
    }

    /**
     * Converte minutos em horas decimais (ex: 90 min -> 1.5h).
     */
    fun calculateDecimalHours(durationMinutes: Int): Double {
        return durationMinutes.toDouble() / 60.0
    }

    /**
     * Calcula o valor total com base nos minutos trabalhados e taxa horária.
     */
    fun calculateTotalValue(durationMinutes: Int, hourlyRate: Double): Double {
        return calculateDecimalHours(durationMinutes) * hourlyRate
    }
}
