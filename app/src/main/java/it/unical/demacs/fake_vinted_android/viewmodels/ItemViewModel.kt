package it.unical.demacs.fake_vinted_android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val localContext: Context) : ViewModel() {

    private val apiService: ApiService
    private val sessionManager: SessionManager

    private val _itemsInVendita = MutableStateFlow<List<Item>>(emptyList())
    val itemsInVendita: StateFlow<List<Item>> = _itemsInVendita.asStateFlow()

    private val _currentItem = MutableStateFlow<Item?>(null)
    val currentItem: StateFlow<Item?> = _currentItem.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadSingleItem(itemId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.getToken() // Assicurati che il token sia passato correttamente
                val response = apiService.getItem(token, itemId)
                if (response.isSuccessful) {
                    _currentItem.value = response.body()
                } else {
                    _error.value = "Errore durante il caricamento dell'item: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete o del server: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }

        }
    }

    init {
        sessionManager = SessionManager(localContext) // Create an instance of your SessionManager class
        apiService = RetrofitClient.create(sessionManager)
    }

    suspend fun getItem(id: Long): Item? {
        _isLoading.value = true
        _error.value = null
        val token = sessionManager.getToken();

        try {
            val response = apiService.getItem(token, id)
            if (response.isSuccessful) {
                _currentItem.value = response.body()
                return response.body()
            } else {
                _error.value = "Errore durante il caricamento dell'item ARCAMADO"
                return null
            }
        } catch (e: Exception) {
            _error.value = "Errore di rete o del server"
            return null
        } finally {
            _isLoading.value = false
        }
    }


    @SuppressLint("SuspiciousIndentation")
    suspend fun fetchItemsInVendita() {
        val token = sessionManager.getToken()
        val response = apiService.getItemInVendita("Bearer $token", token!!)
            if (response.isSuccessful) {
                _itemsInVendita.value = response.body() ?: emptyList()
            }
    }


}