package com.dscorp.ispadmin.data.datasource.remote.auth

import android.content.SharedPreferences
import com.dscorp.ispadmin.data.utils.REMEMBER_CHECKBOX_STATUS
import com.dscorp.ispadmin.data.utils.SESSION_ACCESS_TOKEN
import com.dscorp.ispadmin.data.utils.SESSION_DNI
import com.dscorp.ispadmin.data.utils.SESSION_EMAIL
import com.dscorp.ispadmin.data.utils.SESSION_ID
import com.dscorp.ispadmin.data.utils.SESSION_LAST_NAME
import com.dscorp.ispadmin.data.utils.SESSION_NAME
import com.dscorp.ispadmin.data.utils.SESSION_PASSWORD
import com.dscorp.ispadmin.data.utils.SESSION_PHONE
import com.dscorp.ispadmin.data.utils.SESSION_REFRESH_TOKEN
import com.dscorp.ispadmin.data.utils.SESSION_TYPE
import com.dscorp.ispadmin.data.utils.SESSION_USER_NAME
import com.dscorp.ispadmin.data.utils.SESSION_VERIFIED

class TokenStore(private val prefs: SharedPreferences) {

    fun getAccessToken(): String? = prefs.getString(SESSION_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(SESSION_REFRESH_TOKEN, null)

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        prefs.edit()
            .putString(SESSION_ACCESS_TOKEN, accessToken)
            .putString(SESSION_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(SESSION_ID)
            .remove(SESSION_NAME)
            .remove(SESSION_LAST_NAME)
            .remove(SESSION_USER_NAME)
            .remove(SESSION_PASSWORD)
            .remove(SESSION_DNI)
            .remove(SESSION_EMAIL)
            .remove(SESSION_PHONE)
            .remove(SESSION_TYPE)
            .remove(SESSION_VERIFIED)
            .remove(SESSION_ACCESS_TOKEN)
            .remove(SESSION_REFRESH_TOKEN)
            .remove(REMEMBER_CHECKBOX_STATUS)
            .apply()
    }
}
