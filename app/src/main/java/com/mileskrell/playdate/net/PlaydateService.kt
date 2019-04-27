package com.mileskrell.playdate.net

import com.mileskrell.playdate.api.*
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface PlaydateService {

    /**
     * Creates user record on server. Called whenever a user logs in.
     */
    @PUT("create")
    fun createUser(@Body createRequest: CreateRequest): Deferred<Unit>

    /**
     * Gets list of user's playdates. Called when displaying PlaydateListFragment.
     */
    @GET("list")
    fun listPlaydates(@Body listRequest: ListRequest): Deferred<List<ListResponse>>

    /**
     * Tells server to generate a playdate. Called when someone generates a new playdate.
     */
    @GET("generate")
    fun generatePlaydate(@Body generateRequest: GenerateRequest): Deferred<GenerateResponse>

    /**
     * Gets availability of the playdate creator. Called after entering a playdate code.
     */
    @GET("getAvail")
    fun getAvailability(@Body availabilityRequest: AvailabilityRequest): Deferred<AvailabilityResponse>

    /**
     * Tells server that the user wants to join a playdate. Called once user has specified their own availability
     * (in response to the information received from [getAvailability]
     */
    @GET("join")
    fun joinPlaydate(@Body joinRequest: JoinRequest): Deferred<Unit>

//    @GET("cancel")
//    fun cancelPlaydate(@Body cancelRequest: CancelRequest): Deferred<Unit>
}
