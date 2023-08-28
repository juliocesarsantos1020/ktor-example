package com.example.application.domain.model

import java.util.UUID

class ContactInformation(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val phone: String,
)
