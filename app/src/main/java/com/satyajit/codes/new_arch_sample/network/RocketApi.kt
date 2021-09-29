package com.satyajit.codes.new_arch_sample.network

import com.apollographql.apollo.api.Response
import com.satyajit.codes.new_arch_sample.BookTripMutation
import com.satyajit.codes.new_arch_sample.LaunchDetailsQuery
import com.satyajit.codes.new_arch_sample.LaunchListQuery
import com.satyajit.codes.new_arch_sample.TripsBookedSubscription
import com.satyajit.codes.new_arch_sample.state.ViewState
import kotlinx.coroutines.flow.Flow

interface RocketApi {

    suspend fun queryLaunchesList(): ViewState<LaunchListQuery.Data>?

    suspend fun queryLaunch(id: String): ViewState<LaunchDetailsQuery.Data>?

    suspend fun mutationBookTrip(ids: List<String>): ViewState<BookTripMutation.Data>?

    suspend fun subscribeTripsBooked(): Flow<Response<TripsBookedSubscription.Data>>
}