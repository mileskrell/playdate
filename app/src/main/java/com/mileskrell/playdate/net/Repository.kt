package com.mileskrell.playdate.net

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mileskrell.playdate.api.*
import com.mileskrell.playdate.model.Playdate
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class Repository(baseURL: String, val idToken: String) {

    private val playdateService: PlaydateService

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://$baseURL")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        playdateService = retrofit.create(PlaydateService::class.java)
    }

    suspend fun requestCreateUser(account: GoogleSignInAccount) {
//        playdateService.createUser(CreateRequest(idToken, account.displayName ?: throw RuntimeException("Display name is null"))).await()
    }

    suspend fun requestListPlaydates(account: GoogleSignInAccount): List<Playdate> {
//        val playdates = playdateService.listPlaydates(ListRequest(account.idToken
//            ?: throw RuntimeException("Couldn't get ID token")))
//
//        return playdates.await().map {
//            Playdate(it.name, it.code, it.time, it.duration)
//        }

        delay(1000)
        return listOf(
            Playdate("${account.givenName}'s study group", "ABCD", "2019-03-20T22:15", 120),
            Playdate("very important meeting with a super long title because it's so important", "AUGH", "2019-03-16T16:00", 60),
            Playdate("just chilling", "DEEZ", "2019-03-17T10:00", 30),
            Playdate("hanging out", "HHHH", "2019-03-15T10:30", 15),
            Playdate("${account.familyName} Inc. quarterly planning meeting", "JOKE", "2019-03-15T10:30", 120),
            Playdate("being bored w/ others", "LOLG", "2019-03-16T22:00", 120),
            Playdate("${account.givenName}'s interesting item planning meeting", "WHOO", "2019-03-17T22:15", 45),
            Playdate("working with others", "GGGG", "2019-03-15T07:30", 75),
            Playdate("discrete study group", "FFFF", "2019-03-16T12:30", 120),
            Playdate("hi ${account.givenName}!", "PPPP", "2019-03-17T10:00", 15),
            Playdate("another item", "NNNN", "2019-03-15T01:00", 45),
            Playdate("yet another item", "MMMM", "2019-03-16T22:45", 30),
            Playdate("last one", "OOOO", "2019-04-06T15:15", 60)
        )
    }

    /**
     * Called when user creates a new playdate
     */
    suspend fun requestGeneratePlaydate(duration: Int, startDay: Date, endDay: Date): String {
//        val startDayString = apiDateFormat.format(startDay)
//        val endDayString = apiDateFormat.format(endDay)
//
//        val playdate = playdateService.generatePlaydate(GenerateRequest(idToken, startDayString, endDayString, duration))
//
//        return playdate.await().code

        delay(1000)
        val code = (1..4)
            .map { Random.nextInt(25) }
            .map { ('A'..'Z').toList()[it] }
            .joinToString("")

        return code
    }

    /**
     * Called when uses enters playdate code to join a playdate
     */
    suspend fun requestGetAvailability(code: String): AvailabilityResponse? {
//        val availability = playdateService.getAvailability(AvailabilityRequest(idToken, code)).await()
//
//        if (availability.avail == null) {
//            return null // TODO Is this how we'll indicate that the playdate code is invalid?
//        }
//
//        return availability

        delay(1000)
        return AvailabilityResponse("", "", "")
    }

    /**
     * Called when user submits their time selection for a playdate
     */
    suspend fun requestJoinPlaydate(token: String, playdateCode: String, index: Int) {

//        playdateService.joinPlaydate(JoinRequest(token, playdateCode, index)).await()

        delay(1000)
    }

    /**
     * Called when the user attempts to cancel a playdate
     */
    suspend fun requestCancelPlaydate(code: String): Boolean {
        delay(1000)
        // TODO cancel playdate
        return true
    }
}
