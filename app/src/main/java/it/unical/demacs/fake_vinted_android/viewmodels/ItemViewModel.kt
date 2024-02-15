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
                val response = apiService.getItem("Bearer $token", itemId!!)
                if (response.isSuccessful) {
                    _currentItem.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Errore durante il caricamento dell'item: $itemId, Codice: ${response.code()}"
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
        apiService = RetrofitClient.create( sessionManager,localContext)
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
                _error.value = "Errore durante il caricamento dell'item "
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

        if (token != null) {
            try {
                val response = apiService.getItemInVendita("Bearer $token", token)

                if (response.isSuccessful) {
                    _itemsInVendita.value = response.body() ?: emptyList()
                } else {
                    // Gestisci il caso in cui la richiesta non è stata eseguita correttamente
                    // Ad esempio, logga un messaggio di errore o gestisci l'errore di conseguenza
                }
            } catch (e: Exception) {
                // Gestisci eventuali eccezioni durante la richiesta
                // Ad esempio, logga un messaggio di errore o gestisci l'errore di conseguenza
            }
        } else {
            // Gestisci il caso in cui il token è nullo
            // Ad esempio, logga un messaggio di errore o gestisci l'errore di conseguenza
        }
    }



    fun getItemById(itemId: String): Item? {
        val itemIdLong = itemId.toLongOrNull() // Converte la stringa in Long?, restituisce null se la conversione fallisce
        return itemsInVendita.value.find { it.id == itemIdLong }
    }

}