package it.unical.gciaoo.vinteddu_android.viewmodels

import it.unical.demacs.fake_vinted_android.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class UserState(
    val username: String = "",
    val email: String = "",
    val password: String = "",

    val passwordConfirm: String = "",

    val isUsernameError: Boolean = !User.validateUsername(username = username),
    val isEmailError: Boolean = !User.validateEmail(email = email),
    val isPasswordError: Boolean = !User.validatePassword(password = password),

    val isPasswordConfirmError: Boolean = passwordConfirm.isEmpty()
)

class UserFormViewModel {
    private val _userState = MutableStateFlow(UserState())
    val userState : StateFlow<UserState> = _userState.asStateFlow()

    fun updateUsername(username: String) {
        val hasError = !User.validateUsername(username = username)
        _userState.value = _userState.value.copy(
            username = username,
            isUsernameError = hasError
        )
    }


    fun updateEmail(email: String) {
        val hasError = !User.validateEmail(email = email)
        _userState.value = _userState.value.copy(
            email = email,
            isEmailError = hasError
        )
    }

    fun updatePassword(password: String) {
        val hasError = !User.validatePassword(password = password)
        _userState.value = _userState.value.copy(
            password = password,
            isEmailError = hasError
        )
    }

    fun updatePasswordConfirm(passwordConfirm : String, password : String) {
        val hasError = passwordConfirm.isEmpty() && password != passwordConfirm
        _userState.value = _userState.value.copy(
            passwordConfirm = passwordConfirm,
            isPasswordConfirmError = hasError
        )
    }


}