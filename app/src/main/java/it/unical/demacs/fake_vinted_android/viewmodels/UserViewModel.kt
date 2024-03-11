package it.unical.demacs.fake_vinted_android.viewmodels

import android.content.Context
import android.net.Uri
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.User
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserViewModel(private val localContext: Context): ViewModel() {

    private val apiService: ApiService
    private val sessionManager: SessionManager

    private val _user = MutableStateFlow<UtenteDTO?>(null)
    val user: StateFlow<UtenteDTO?> = _user.asStateFlow()

    private val _saldo = MutableStateFlow<Wallet?>(null)
    val saldo: StateFlow<Wallet?> = _saldo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        sessionManager = SessionManager(localContext)
        apiService = RetrofitClient.create(sessionManager,localContext)
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.getToken()
                val response = apiService.getCurrentUser("Bearer $token", token)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    Log.d("eccolo", _user.value.toString())
                } else {

                    val errorBody = response.errorBody()?.string()
                    _error.value = "Errore durante il recupero dell'utente: Codice: ${response.code()}, Messaggio: $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete o del server: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getsaldo(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.getToken()
                val response = apiService.getSaldo("Bearer $token", token)
                if (response.isSuccessful) {
                    _saldo.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Errore durante il recupero dell'utente: Codice: ${response.code()}, Messaggio: $errorBody"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete o del server: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }

    }
}