package com.satyajit.codes.new_arch_sample.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Logger
import com.satyajit.codes.new_arch_sample.Utils
import com.satyajit.codes.new_arch_sample.state.ViewState
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RocketServiceTest {

    private val mockWebServer = MockWebServer()

    private lateinit var apolloClient: ApolloClient

    private lateinit var objectUnderTest: RocketService

    @Before
    fun setUp() {
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .dispatcher(Dispatcher(Utils.immediateExecutorService()))
            .build()

        apolloClient = ApolloClient.builder()
            .serverUrl(mockWebServer.url("/"))
            .dispatcher(Utils.immediateExecutor())
            .okHttpClient(okHttpClient)
            .logger(object : Logger {
                override fun log(priority: Int, message: String, t: Throwable?, vararg args: Any) {
                    println(String.format(message, *args))
                    t?.printStackTrace()
                }
            }).build()

        objectUnderTest = RocketService(apolloClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetchLaunchesListFromNetworkTest() = runBlocking {
        mockWebServer.enqueue(Utils.mockResponse("launch_list_response.json"))

        val response = objectUnderTest.queryLaunchesList()
        val responseBody = requireNotNull((response as ViewState.Success).result)
        mockWebServer.takeRequest()

        assertThat(responseBody.launches.launches.size, CoreMatchers.`is`(2))
        assertThat(responseBody.launches.launches[1]?.id, CoreMatchers.`is`("108"))
    }

    @Test
    fun fetchLaunchInfoFromNetworkTest() = runBlocking {
        mockWebServer.enqueue(Utils.mockResponse("launch_details_response.json"))
        val response = objectUnderTest.queryLaunch("109")
        val responseBody = requireNotNull((response as ViewState.Success).result)
        mockWebServer.takeRequest()

        assertThat(responseBody.launch?.id, CoreMatchers.`is`("109"))
        assertThat(responseBody.launch?.isBooked, CoreMatchers.`is`(false))
        assertThat(responseBody.launch?.site, CoreMatchers.`is`("CCAFS SLC 40"))
    }

    @Test
    fun doBookingTrip() = runBlocking {
        mockWebServer.enqueue(Utils.mockResponse("book_trip_response.json"))
        val response = objectUnderTest.mutationBookTrip(listOf("100"))
        val responseBody = requireNotNull((response as ViewState.Success).result)
        mockWebServer.takeRequest()

        assertThat(responseBody.bookTrips.success, CoreMatchers.`is`(true))
        assertThat(responseBody.bookTrips.message, CoreMatchers.`is`("trips booked successfully"))
        assertThat(responseBody.bookTrips.launches?.get(0)?.id, CoreMatchers.`is`("100"))
    }

}