package com.satyajit.codes.new_arch_sample.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.satyajit.codes.new_arch_sample.Constants.TOKEN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", TOKEN)
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(okHttpClient: OkHttpClient): ApolloClient {
        return ApolloClient.builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(
                    "wss://apollo-fullstack-tutorial.herokuapp.com/graphql",
                    okHttpClient
                )
            )
            .okHttpClient(okHttpClient)
            .build()
    }

}