package com.satyajit.codes.new_arch_sample.network

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.satyajit.codes.new_arch_sample.BookTripMutation
import com.satyajit.codes.new_arch_sample.LaunchDetailsQuery
import com.satyajit.codes.new_arch_sample.LaunchListQuery
import com.satyajit.codes.new_arch_sample.TripsBookedSubscription
import com.satyajit.codes.new_arch_sample.state.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

import javax.inject.Inject

class RocketService @Inject constructor(private val apolloClient: ApolloClient) : RocketApi,
    BaseService() {

    private val TAG = "RocketService"

    override suspend fun queryLaunchesList(): ViewState<LaunchListQuery.Data>? {
        var result: ViewState<LaunchListQuery.Data>? = null
        try {
            val response = apolloClient.query(LaunchListQuery()).await()
            response.let {
                it.data?.let { data ->
                    result = handleSuccess(data)
                }
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return result
    }

    override suspend fun queryLaunch(id: String): ViewState<LaunchDetailsQuery.Data>? {
        var result: ViewState<LaunchDetailsQuery.Data>? = null
        try {
            val response = apolloClient.query(LaunchDetailsQuery(id)).await()
            response.let {
                it.data?.let { data ->
                    result = handleSuccess(data)
                }
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return result
    }

    override suspend fun mutationBookTrip(ids: List<String>): ViewState<BookTripMutation.Data>? {
        var result: ViewState<BookTripMutation.Data>? = null
        try {
            val response = apolloClient.mutate(BookTripMutation(ids)).await()
            response.let {
                it.data?.let { data ->
                    result = handleSuccess(data)
                }
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "Error: ${e.message}")
            return handleException(GENERAL_ERROR_CODE)
        }
        return result
    }

    @ExperimentalCoroutinesApi
    override suspend fun subscribeTripsBooked(): Flow<Response<TripsBookedSubscription.Data>> {
        return apolloClient.subscribe(TripsBookedSubscription()).toFlow().retryWhen { _, attempt ->
            delay(attempt * 1000)
            true
        }
    }
}