package it.unical.demacs.fake_vinted_android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ItemViewModel(private val localContext: Context) : ViewModel() {

    private val apiService: ApiService
    private val sessionManager: SessionManager

    private val _itemsInVendita = MutableStateFlow<List<Item>>(emptyList())
    val itemsInVendita: StateFlow<List<Item>> = _itemsInVendita.asStateFlow()

    init {
        sessionManager = SessionManager(localContext) // Create an instance of your SessionManager class
        apiService = RetrofitClient.create(sessionManager)
    }

    suspend fun getItem(id: Long): Item? {
        val token = sessionManager.getToken();
        val response = apiService.getItem("token", id)

        return if (response.isSuccessful) {
            response.body()
        } else {
            null
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