package it.unical.demacs.fake_vinted_android.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item

class ItemViewModel(private val localContext: Context) : ViewModel() {

    private val apiService: ApiService
    private val sessionManager: SessionManager

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
}