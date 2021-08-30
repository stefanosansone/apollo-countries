package app.countries.data

import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Apollo {
    @Singleton
    @Provides
    fun getClient(): ApolloClient {
        return ApolloClient.builder()
            .serverUrl("https://countries.trevorblades.com")
            .build()
    }
}