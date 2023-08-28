package com.example.application.domain.model

import java.time.Instant
import java.util.UUID

class CarDetails(
    val id: UUID = UUID.randomUUID(),
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val color: String,
    val createdAt: Instant = Instant.now(),
)
