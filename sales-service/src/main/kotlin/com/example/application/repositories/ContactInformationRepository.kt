package com.example.application.repositories

import com.example.application.domain.model.ContactInformation

interface ContactInformationRepository {
    suspend fun save(contactInformation: ContactInformation): ContactInformation?
}
