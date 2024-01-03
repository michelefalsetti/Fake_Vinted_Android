package it.unical.demacs.fake_vinted_android.ApiConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val response = chain.proceed(originalRequest)
        val token = response.header("Authorization")


        if (token != null) {
            sessionManager.saveToken(token)
        }

        return response
    }

}
