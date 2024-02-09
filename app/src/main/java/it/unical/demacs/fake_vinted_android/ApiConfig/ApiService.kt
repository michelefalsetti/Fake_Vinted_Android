package it.unical.demacs.fake_vinted_android.ApiConfig
import android.app.Notification
import androidx.compose.ui.text.input.TextFieldValue
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.Notifications
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.math.BigDecimal

interface ApiService {



    @GET("/api/v2/user/{token}")
    suspend fun getCurrentUser(@Header("Authorization") token:String?, @Path("token") token_: String?) : Response<UtenteDTO>

    @GET("/api/v2/wallet/{token}")
    suspend fun getSaldo(@Header("Authorization") token:String?, @Path("token") token_: String?) : Response<Wallet>


    @FormUrlEncoded
    @POST("/api/v1/authenticate")
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<Unit>

    @FormUrlEncoded
    @POST("/api/v1/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("nome") nome: String,
        @Field("cognome") cognome: String,
    ): Response<String>

    @GET("/api/v3/item/{itemId}")
    suspend fun getItem(@Header("Authorization") token:String?, @Path("itemId") itemId: Long): Response<Item>

    @GET("/api/v3/{token}/notification")
    suspend fun getUserNotification(@Header("Authorization") token: String?,@Path("token") token_: String?): Response<List<Notifications>>

    @GET("/api/v3/search/{nome}/{token}")
    suspend fun getSearch(@Header("Authorization") token:String?, @Path("nome") nome: String, @Path("token") token_: String?): Response<List<Item>>
    @GET("/api/v2/Favorites/{token}")
    suspend fun getFavorites(@Header("Authorization") token:String?, @Path("token") token_: String): Response<List<Item>>

    @GET("/api/v2/inVendita/{token}")
    suspend fun getItemInVendita(@Header("Authorization") token: String?, @Path("token") token_: String): Response<List<Item>>


    @FormUrlEncoded
    @POST("/api/v3/add/{token}")
    suspend fun addItem(
        @Header("Authorization") token:String?,
        @Path("token") token_: String?,
        @Field("nome") nome: String,
        @Field("descrizione") descrizione: String,
        @Field("prezzo") prezzo: BigDecimal,
        @Field("immagine") immagine: String?,
        @Field("categoria") categoria : String,
        @Field("condizioni") condizioni : String
    ): Response<String>

    @FormUrlEncoded
    @POST("/api/v1/newPassword")
    suspend fun getNewPassword(
        @Field("email") email: String,
        @Field("username") username: String,
    ): Response<String>


    @FormUrlEncoded
    @POST("/api/v1/buyItem/{item}")
    suspend fun buyItem(@Header("Authorization") token:String?,  @Path("item") itemId: Long?, @Field("token") token_: String?) : Response<Unit>
    @GET("/api/v1/SaldoOk/{itemId}/{token}")
    suspend fun saldo(@Header("Authorization") token:String?, @Path("itemId") itemId: Long, @Path("token") token_: String?): Response<Unit>

    @GET("/api/v2/getPurchase/{token}")
    suspend fun getItemAcquistati(@Header("Authorization") token:String?, @Path("token") token_: String?): Response<List<Item>>



    @FormUrlEncoded
    @POST("api/v3/{token}/addUserNotification")
    suspend fun addUserNotification(@Header("Authorization") token: String?, @Path("token") pathToken: String?, @Field("idUtente") idUtente: Long): Response<String>

}
