package com.example.application.domain.model

import java.time.Instant
import java.util.UUID

class Sales(
    val id: UUID = UUID.randomUUID(),
    val buyerName: String,
    val carDetails: CarDetails,
    val price: Int,
    val paymentMethod: PaymentMethod,
    val contactInformation: ContactInformation,
    val createdAt: Instant = Instant.now(),
    val deleted: Boolean = false,
)

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    BANK_TRANSFER,
    FINANCING,
}
