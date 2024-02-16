package it.unical.demacs.fake_vinted_android.model

import java.time.LocalDate

class User(
    private val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: LocalDate,
    val address: String,
    val phoneNumber: String,

) {

    companion object {
        fun validateUsername(username: String): Boolean {
            if (username.length in 1..20) {
                return true
            }
            return false
        }

        fun validateFirstName(firstName: String): Boolean {
            if (firstName.length in 1..50)
                return true
            return false
        }

        fun validateLastName(lastName: String): Boolean {
            if (lastName.length in 1..50)
                return true
            return false
        }

        fun validateEmail(email: String): Boolean {
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return true
            return false
        }



        fun validatePassword(password: String): Boolean {
            if (password.isNotEmpty()) {
                if (password.contains(Regex("[a-z]")) and password.contains(Regex("[A-Z]")) and password.contains(
                        Regex("[0-9]")
                    )
                ) {
                    return true
                }
            }
            return false
        }


    }
}

