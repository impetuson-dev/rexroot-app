package com.impetuson.rexroot.viewmodel.onboarding

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginValidatorTest {
    @Test
    fun validateInput() {
        val userEmail: String = "tharunbalaji31@gmail.com"
        val userPassword: String = "tharun2004"
        val result = LoginValidator.validateInput(userEmail, userPassword)

        assertEquals(true, result)
    }
}