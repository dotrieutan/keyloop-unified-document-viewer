package com.keyloop.documentviewer.domain

@JvmInline
value class Vin private constructor(
    val value: String,
) {
    companion object {
        private val format = Regex("^[A-HJ-NPR-Z0-9]{17}$")

        fun parse(raw: String): Vin {
            val normalized = raw.trim().uppercase()
            require(format.matches(normalized)) {
                "VIN must contain exactly 17 characters and cannot contain I, O, or Q"
            }
            return Vin(normalized)
        }
    }
}
