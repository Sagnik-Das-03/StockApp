package com.test.stockmarketapp.data.repository

import com.test.stockmarketapp.data.csv.CSVParser
import com.test.stockmarketapp.data.csv.IntraDayInfoParser
import com.test.stockmarketapp.data.local.StockDatabase
import com.test.stockmarketapp.data.mapper.toCompanyInfo
import com.test.stockmarketapp.data.mapper.toCompanyListing
import com.test.stockmarketapp.data.mapper.toCompanyListingEntity
import com.test.stockmarketapp.data.remote.StockApi
import com.test.stockmarketapp.domain.model.CompanyInfo
import com.test.stockmarketapp.domain.model.CompanyListing
import com.test.stockmarketapp.domain.model.IntradayInfo
import com.test.stockmarketapp.domain.repository.StockRepository
import com.test.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intraDayInfoParser: CSVParser<IntradayInfo>
): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch(e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val respone = api.getIntradayInfo(symbol)
            val results = intraDayInfoParser.parse(respone.byteStream())
            Resource.Success(results)
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load Intraday Info type: IOException"
            )
        }catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load Intraday Info type: HttpException"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        } catch (e: IOException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load Intraday Info type: IOException"
            )
        }catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(
                message = "Couldn't load Intraday Info type: HttpException"
            )
        }
    }
}