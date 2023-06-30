package com.impetuson.rexroot

import com.impetuson.rexroot.viewmodel.onboarding.LoginValidator
import org.junit.Test

import org.junit.Assert.*
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(JUnit4::class)
class ExampleUnitTest {
    @Test
    fun loginValidator() {
        val userEmail: String = "abc123@gmail.com"
        val userPassword: String = "abc2004"
        val result = LoginValidator.validateInput(userEmail, userPassword)

        Assertions.assertEquals(true, result)
    }
}