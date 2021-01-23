package de.othr.archeologicalfieldwork.helper

import android.util.Patterns

enum class AccountInputStatus {
    INVALID_PASSWORD, INVALID_USERNAME, VALID
}

fun checkAccountInput(inputUsername: String, inputPassword: String): AccountInputStatus {
    if (inputUsername != null && !isValidUsername(inputUsername)) {
        return AccountInputStatus.INVALID_USERNAME
    }

    if (inputPassword != null && inputPassword.isNotBlank() && !isValidPassword(inputPassword)) {
        return AccountInputStatus.INVALID_PASSWORD
    }

    return AccountInputStatus.VALID
}

private fun isValidPassword(password: String): Boolean {
    return password.length > 5
}

private fun isValidUsername(username: String): Boolean {
    return if (username.contains('@')) {
        Patterns.EMAIL_ADDRESS.matcher(username).matches()
    } else {
        username.isNotBlank()
    }
}