package com.example.application.repositories

import com.example.application.domain.model.CarDetails

interface CarDetailsRepository {
    suspend fun save(cardDetails: CarDetails): CarDetails?
}
