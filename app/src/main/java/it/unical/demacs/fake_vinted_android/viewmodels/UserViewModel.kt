package it.unical.demacs.fake_vinted_android.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.User
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class UserViewModel(private val context: Context): ViewModel() {

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

    fun updateBirthDate(birthDate: LocalDate) {
        val hasError = !UtenteDTO.validateBirthDate(birthDate)
        _userState.value = _userState.value.copy(
            birthDate = birthDate,
            isBirthDateError = hasError
        )
    }

    fun updatePhoneNumber(phoneNumber: String) {
        val hasError = !UtenteDTO.validatePhoneNumber(phoneNumber)
        _userState.value = _userState.value.copy(
            phoneNumber = phoneNumber,
            isPhoneNumberError = hasError
        )
    }

    fun updateProfileImage(uri: Uri) {// Converti l'Uri in String
        val uriString = uri.toString()

        // Salvare l'URI come stringa in SharedPreferences o nel database
        val sharedPref = context.getSharedPreferences("Profile_Preferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("profile_image_uri", uriString)
            apply()
        }

        // Aggiorna lo stato locale se necessario
        //_userState.value = userState.value.copy(profileImageUrl = uriString)
    }
}