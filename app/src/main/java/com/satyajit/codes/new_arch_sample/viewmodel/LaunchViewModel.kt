package com.satyajit.codes.new_arch_sample.viewmodel

import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satyajit.codes.new_arch_sample.*
import com.satyajit.codes.new_arch_sample.network.BaseService
import com.satyajit.codes.new_arch_sample.network.RocketService
import com.satyajit.codes.new_arch_sample.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val service: RocketService
) : ViewModel() {
    private val _launchList by lazy { MutableLiveData<ViewState<LaunchListQuery.Data>>() }
    val launchList: LiveData<ViewState<LaunchListQuery.Data>>
        get() = _launchList

    private val _launchDetails by lazy { MutableLiveData<ViewState<LaunchDetailsQuery.Data>>() }
    val launchDetails: LiveData<ViewState<LaunchDetailsQuery.Data>>
        get() = _launchDetails

    private val _tripBookingDetails by lazy { MutableLiveData<String>() }
    val tripBookingDetails: LiveData<String>
        get() = _tripBookingDetails

    private val _bookTripDetails by lazy { MutableLiveData<ViewState<BookTripMutation.Data>>() }
    val bookTripDetails: LiveData<ViewState<BookTripMutation.Data>>
        get() = _bookTripDetails

    fun queryLaunchList() {
        _launchList.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = service.queryLaunchesList()
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _launchList.postValue(data)
                        Log.d("queryLaunchList()", "response: $data")
                    }
                    is ViewState.Error -> {
                        _launchList.postValue(data)
                        Log.e("queryLaunchList()", "error block")
                    }
                    else -> {
                        _launchList.postValue(data)
                        Log.e("queryLaunchList()", "catch block")
                    }
                }

            }
        }
    }

    fun queryLaunchDetails(id: String) {
        _launchDetails.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = service.queryLaunch(id)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _launchDetails.postValue(data)
                        Log.d("queryLaunchDetails(id)", "response: $data")
                    }
                    is ViewState.Error -> {
                        _launchDetails.postValue(data)
                        Log.e("queryLaunchDetails(id)", "error block")
                    }
                    else -> {
                        _launchDetails.postValue(data)
                        Log.e("queryLaunchDetails(id)", "catch block")
                    }
                }
            }
        }
    }


    fun mutateTripBookDetails(ids: List<String>) {
        _bookTripDetails.postValue(ViewState.Loading)
        viewModelScope.launch {
            val response = service.mutationBookTrip(ids)
            response.let { data ->
                when (data) {
                    is ViewState.Success -> {
                        _bookTripDetails.postValue(data)
                        Log.d("mutateTripBooking", "response: $data")
                    }
                    is ViewState.Error -> {
                        _bookTripDetails.postValue(data)
                        Log.e("mutateTripBooking", "error block")
                    }
                    else -> {
                        _bookTripDetails.postValue(data)
                        Log.e("mutateTripBooking", "catch block")
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun subscribeToTrip() {
        viewModelScope.launch {
            service.subscribeTripsBooked().collect {
                val text = when (val trips = it.data?.tripsBooked) {
                    null -> BaseService.SOMETHING_WRONG
                    -1 -> "Trip Cancelled"
                    else -> "$trips trip(s) has(have) been booked"
                }

                _tripBookingDetails.postValue(text)
            }
        }
    }
}