package com.mileskrell.playdate.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mileskrell.playdate.R
import com.mileskrell.playdate.api.AvailabilityResponse
import com.mileskrell.playdate.net.*
import java.util.*

class UserViewModel(app: Application) : AndroidViewModel(app) {

    val context: Context = getApplication<Application>().applicationContext
    val googleSignInClient: GoogleSignInClient

    lateinit var account: GoogleSignInAccount
    fun accountIsInitialized() = ::account.isInitialized

    private val _playdates: MutableLiveData<List<Playdate>> = MutableLiveData()
    val playdates: LiveData<List<Playdate>>
        get() = _playdates

    lateinit var repository: Repository

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun storeUserAndInitRepo(possibleAccount: GoogleSignInAccount) {
        repository = Repository(context.getString(R.string.web_client_id), possibleAccount.idToken
            ?: throw RuntimeException("Could not init repository: user ID token is null")) // TODO Is this the base URL?
        account = possibleAccount
    }

    suspend fun sendUser() {
        repository.requestCreateUser(account)
    }

    fun logOut() {
        googleSignInClient.signOut()

//        googleSignInClient.signOut().addOnCompleteListener {
//            _account.value = null
//        }
    }

    fun debugClearPlaydates() {
        _playdates.value = listOf()
    }

    suspend fun getPlaydates() {
        _playdates.postValue(repository.requestListPlaydates(account))
    }

    suspend fun createPlaydate(name: String, duration: Int, startDay: Date, endDay: Date): String {
        repository.requestGeneratePlaydate(duration, startDay, endDay).let { code ->
            _playdates.postValue(_playdates.value?.plus(Playdate(name, code, null, duration)))
            return code
        }
    }

    suspend fun getAvailability(code: String): AvailabilityResponse? {
        return repository.requestGetAvailability(code)
    }

    suspend fun cancelPlaydate(playdate: Playdate): Boolean {
        return repository.requestCancelPlaydate(playdate.code).also { success ->
            if (success) {
                _playdates.postValue(_playdates.value?.minus(playdate))
            }
        }
    }
}
