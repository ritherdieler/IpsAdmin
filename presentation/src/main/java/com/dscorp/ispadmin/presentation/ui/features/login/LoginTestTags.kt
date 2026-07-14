object LoginTestTags {
    const val USERNAME = "login_username"
    const val PASSWORD = "login_password"
    const val PASSWORD_VISIBILITY = "login_password_visibility"
    const val REMEMBER_SESSION = "login_remember_session"
    const val SUBMIT = "login_submit"
    const val BIOMETRIC = "login_biometric"
    const val CREATE_ACCOUNT = "login_create_account"

    val interactive = listOf(
        USERNAME,
        PASSWORD,
        PASSWORD_VISIBILITY,
        REMEMBER_SESSION,
        SUBMIT,
        BIOMETRIC,
        CREATE_ACCOUNT
    )
}

object LoginContentDescriptions {
    const val LOGO = "Logo de ISP Admin"
    const val BIOMETRIC_ICON = "Huella digital"
}
