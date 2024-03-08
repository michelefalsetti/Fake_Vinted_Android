package it.unical.demacs.fake_vinted_android.model

import android.graphics.Bitmap
import java.math.BigDecimal



data class Item(

    val id: Long?,
    var nome: String,
    var descrizione: String?,
    var prezzo: Long?,
    val dataCreazione: List<Int>?,
    val stato: String,
    val idUtente: Long?,
    val nomeutente: String?,
    var immagini: String?,
    var categoria: String,
    var condizioni: String
)