package com.test.stockmarketapp.presentation.domain.repository

import com.test.stockmarketapp.presentation.domain.model.CompanyListing
import com.test.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {

    suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}