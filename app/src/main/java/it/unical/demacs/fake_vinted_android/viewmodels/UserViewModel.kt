package it.unical.gciaoo.vinteddu_android.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(): ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState : StateFlow<UserState> = _userState.asStateFlow()

    fun updateUsername(username: String) {
        val hasError = !UtenteDTO.validateUsername(username = username)
        _userState.value = _userState.value.copy(
            username = username,
            isUsernameError = hasError
        )
    }

    fun updateFirstName(firstName: String) {
        val hasError = !UtenteDTO.validateFirstName(firstName = firstName)
        _userState.value = _userState.value.copy(
            firstName = firstName,
            isFirstNameError = hasError
        )
    }

    fun updateLastName(lastName : String) {
        val hasError = !UtenteDTO.validateLastName(lastName = lastName)
        _userState.value = _userState.value.copy(
            lastName = lastName,
            isLastNameError = hasError
        )
    }

    fun updateEmail(email: String) {
        val hasError = !UtenteDTO.validateEmail(email = email)
        _userState.value = _userState.value.copy(
            email = email,
            isEmailError = hasError
        )
    }


    fun updatePhoneNumber(phoneNumber: String) {
        val hasError = !UtenteDTO.validatePhoneNumber(phoneNumber)
        _userState.value = _userState.value.copy(
            phoneNumber = phoneNumber,
            isPhoneNumberError = hasError
        )
    }
}