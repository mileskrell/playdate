package com.mileskrell.playdate.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.mileskrell.playdate.R
import com.mileskrell.playdate.model.UserViewModel
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Login page
 *
 */
class LoginFragment : Fragment() {
    private val RC_SIGN_IN = 1

    lateinit var userViewModel: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sign_in_button.setSize(SignInButton.SIZE_WIDE)

        userViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)

        sign_in_button.setOnClickListener {
            startActivityForResult(userViewModel.googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                // If we've successfully signed in, we can leave
                findNavController().popBackStack(R.id.playdate_list_dest, false)
            } catch (e: ApiException) {
                if (e.statusCode != 4) {
                    Snackbar.make(login_constraint_layout, "Google API Exception", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}
