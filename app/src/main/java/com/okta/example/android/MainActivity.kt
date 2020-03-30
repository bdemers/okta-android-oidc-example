package com.okta.example.android

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.okta.oidc.*
import com.okta.oidc.clients.sessions.SessionClient
import com.okta.oidc.clients.web.WebAuthClient
import com.okta.oidc.storage.security.DefaultEncryptionManager
import com.okta.oidc.util.AuthorizationException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    /**
     * Authorization client using chrome custom tab as a user agent.
     */
    private lateinit var webAuth: WebAuthClient
    /**
     * The authorized client to interact with Okta's endpoints.
     */
    private lateinit var sessionClient: SessionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWebAuth()
        setupWebAuthCallback(webAuth)

        signIn.setOnClickListener {
            val payload = AuthenticationPayload.Builder()
                .build()
            webAuth.signIn(this, payload)
        }

    }

    private fun setupWebAuth() {

        val oidcConfig = OIDCConfig.Builder()
            .clientId("0oaqlwii1hJm0x2oF0h7")
            .redirectUri("com.oktapreview.dev-259824:/callback")
            .endSessionRedirectUri("com.oktapreview.dev-259824:/logout")
            .scopes("openid", "profile", "offline_access")
            .discoveryUri("https://dev-259824-admin.oktapreview.com")
            .create()

        webAuth = Okta.WebAuthBuilder()
            .withConfig(oidcConfig)
            .withContext(applicationContext)
            .withCallbackExecutor(null)
            .withEncryptionManager(DefaultEncryptionManager(this))
            .setRequireHardwareBackedKeyStore(true)
            .create()
        sessionClient = webAuth.sessionClient
    }

    private fun setupWebAuthCallback(webAuth: WebAuthClient) {
        val callback: ResultCallback<AuthorizationStatus, AuthorizationException> =
            object : ResultCallback<AuthorizationStatus, AuthorizationException> {
                override fun onSuccess(status: AuthorizationStatus) {
                    if (status == AuthorizationStatus.AUTHORIZED) {
                        Log.d("MainActivity", "AUTHORIZED")
                    } else if (status == AuthorizationStatus.SIGNED_OUT) {
                        Log.d("MainActivity", "SIGNED_OUT")
                    }
                }

                override fun onCancel() {
                    Log.d("MainActivity", "CANCELED")
                }

                override fun onError(msg: String?, error: AuthorizationException?) {
                    Log.d("MainActivity", "${error?.error} onError", error)
                }
            }
        webAuth.registerCallback(callback, this)
    }

}
