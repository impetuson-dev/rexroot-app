package com.impetuson.rexroot.viewmodel.onboarding

// For Login Unit Testing
object LoginValidator {

    fun validateInput(email: String, password: String): Boolean {
        var emailValidation = false
        var passwordValidation = false

        if (email.isNotEmpty() && isEmailValid(email)) {
            emailValidation = true
        }

        if (password.isNotEmpty()){
            passwordValidation = true
        }

        return emailValidation && passwordValidation
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return email.matches(emailRegex)
    }

}