package com.test.stockmarketapp.di

import com.test.stockmarketapp.data.csv.CSVParser
import com.test.stockmarketapp.data.csv.CompanyListingsParser
import com.test.stockmarketapp.data.csv.IntraDayInfoParser
import com.test.stockmarketapp.data.repository.StockRepositoryImpl
import com.test.stockmarketapp.domain.model.CompanyListing
import com.test.stockmarketapp.domain.model.IntradayInfo
import com.test.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntradayInoParser(
        intraDayInfoParser: IntraDayInfoParser
    ): CSVParser<IntradayInfo>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}