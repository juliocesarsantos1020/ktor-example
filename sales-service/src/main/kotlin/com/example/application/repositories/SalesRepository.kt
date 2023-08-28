package com.example.application.repositories

import com.example.application.domain.model.Sales

interface SalesRepository {
    suspend fun save(sales: Sales): Sales?
}
