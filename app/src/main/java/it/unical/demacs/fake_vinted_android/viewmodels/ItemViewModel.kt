package it.unical.demacs.fake_vinted_android.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Favorites
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

    private val _favorites = MutableStateFlow<Set<Favorites>>(emptySet())
    val favorites: StateFlow<Set<Favorites>> = _favorites


    fun loadSingleItem(itemId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = sessionManager.getToken()
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
        sessionManager = SessionManager(localContext)
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
                }
            } catch (e: Exception) {
            }
        } else {
        }
    }

    fun sortItemsByPrice(descending: Boolean) {
        val currentItems = _itemsInVendita.value.toMutableList()
        currentItems.sortBy { it.prezzo }
        if (descending) {
            currentItems.reverse()
        }
        _itemsInVendita.value = currentItems
    }





fun getItemById(itemId: String): Item? {
        val itemIdLong = itemId.toLongOrNull()
        return itemsInVendita.value.find { it.id == itemIdLong }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val token = sessionManager.getToken()
            if (token != null) {
                val userResponse = apiService.getCurrentUser("Bearer $token", token)
                if (userResponse.isSuccessful) {
                    val userId = userResponse.body()?.id
                    if (userId != null) {
                        val response = apiService.getFavorites("Bearer $token", userId)
                        if (response.isSuccessful) {
                            _favorites.value = response.body()?.toSet() ?: emptySet()
                        } else {
                        }
                    } else {
                    }
                } else {
                }
            }
        }
    }
}