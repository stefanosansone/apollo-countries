package app.countries.data

import app.countries.CountriesListQuery
import app.countries.CountryDetailQuery
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getCountriesList(): List<CountriesListQuery.Country> {
        val response =
            apolloClient.query(CountriesListQuery()).await()
        return if (!response.hasErrors() && response.data?.countries != null) {
            response.data?.countries!!
        } else {
            throw Exception(response.errors?.toString())
        }
    }

    suspend fun getCountryDetail(code: String): CountryDetailQuery.Country {
        val response = apolloClient.query(
            CountryDetailQuery(code = code)).await()
        return if (!response.hasErrors() && response.data?.country != null) {
            response.data?.country!!
        } else {
            throw Exception(response.errors?.toString())
        }
    }
}