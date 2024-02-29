package it.unical.demacs.fake_vinted_android.ApiConfig
import it.unical.demacs.fake_vinted_android.model.Address
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.Favorites
import it.unical.demacs.fake_vinted_android.model.Notifications
import it.unical.demacs.fake_vinted_android.model.Offer
import it.unical.demacs.fake_vinted_android.model.User
import it.unical.demacs.fake_vinted_android.model.Utente
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
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
        @Field("indirizzo") indirizzo: String,
        @Field("via") via:String,
        @Field("numerocivico")numerocivico:String,
        @Field("cap") cap: String,
        @Field("citta")citta: String,
        @Field("provincia")provincia: String
    ): Response<Utente>

    @GET("/api/v3/item/{itemId}")
    suspend fun getItem(@Header("Authorization") token:String?, @Path("itemId") itemId: Long): Response<Item>

    @GET("/api/v3/{token}/notification")
    suspend fun getUserNotification(@Header("Authorization") token: String?,@Path("token") token_: String?): Response<List<Notifications>>

    @GET("/api/v3/search/{nome}/{token}")
    suspend fun getSearch(@Header("Authorization") token:String?, @Path("nome") nome: String, @Path("token") token_: String?): Response<List<Item>>
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
    ): Response<Item>


    @FormUrlEncoded
    @POST("/api/v1/buyItem/{item}")
    suspend fun buyItem(@Header("Authorization") token:String?, @Field("username") username :String?,@Path("item") itemId: Long?, @Field("token") token_: String?, @Field("prezzoTotale") prezzoTotale: Double?) : Response<Unit>

    @FormUrlEncoded
    @POST("/api/v1/makeOffer")
    suspend fun makeOffer(
        @Header("Authorization") token: String?,
        @Field("idofferente") idOfferente: Long?,
        @Field("idproprietario") idProprietario: Long?,
        @Field("idprodotto") idProdotto: Long?,
        @Field("offerta") offerta: Int?,
        @Field("usernameutenteofferente") usernameutenteofferente:String?,
        @Field("nomeprodotto") nomeprodotto:String?,
    ): Response<Unit>
    @GET("/api/v2/getPurchase/{token}")
    suspend fun getItemAcquistati(@Header("Authorization") token:String?, @Path("token") token_: String?): Response<List<Item>>


    @FormUrlEncoded
    @POST("/api/v3/addUserNotification/{token}")
    suspend fun addUserNotification(
        @Header("Authorization") token: String?,
        @Path("token") token_: String?,
        @Field("idUtente") idUtente: Long,
        @Field("messaggio") messaggio: String
    ): Response<Notifications>

    @GET("/api/v2/getAddress/{token}")
    suspend fun getIndirizzo(@Header("Authorization") token:String?, @Path("token") token_: String?): Response<Address>

    @GET("/api/v1/getOffers/{userId}")
    suspend fun getOffersByUserId(@Header("Authorization") token: String?, @Path("userId") userId: Long): Response<List<Offer>>

    @DELETE("/api/v1/deleteOffer/{idprodotto}")
    suspend fun deleteOffer(@Header("Authorization") token: String?, @Path("idprodotto") idprodotto: Long): Response<Unit>

    @GET("/api/v1/getFavorites/{idUtente}")
    suspend fun getFavorites(@Header("Authorization") token: String?, @Path("idUtente") idUtente: Long): Response<List<Favorites>>

    @FormUrlEncoded
    @POST("/api/v1/addToFavorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String?,
        @Field("idutente") idutente: Long?,
        @Field("idprodotto") idprodotto: Long?
    ): Response<Unit>

    @DELETE("/api/v1/removeFromFavorites/{idutente}/{idprodotto}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String?,
        @Path("idutente") idutente: Long?,
        @Path("idprodotto") idprodotto: Long?
    ): Response<Unit>



}
