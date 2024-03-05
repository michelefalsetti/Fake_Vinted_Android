package it.unical.demacs.fake_vinted_android.ApiConfig

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SessionManager", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    private val secretKey=  "23778sah9021-12123-12s-as-12a-AS_12xoiJN-SHWQ98";

    fun saveUsername(username : String) {
        editor.putString("Username", username)
        editor.apply()
    }


    fun getUsername() : String? {
        val id = sharedPreferences.getString("Username", null)

        return id;
    }


    fun clearUsername() {
        editor.remove("Username")
        editor.apply()
    }

    fun saveToken(token: String) {
        editor.putString("Bearer", token)
        editor.apply()
    }

    fun getToken(): String? {
        val token = sharedPreferences.getString("Bearer", null)
        return token?.replace("Bearer ", "")
    }

    fun clearToken() {
        editor.remove("Bearer")
        editor.apply()
    }
    fun logout() {
        clearUsername()
        clearId()
        clearToken()
    }

    fun clearId() {
        editor.remove("Id")
        editor.apply()
    }

    fun saveThemePreference(isDarkTheme: Boolean) {
        editor.putBoolean("dark_theme", isDarkTheme).apply()
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false) // Default è false per il tema chiaro
    }

}
