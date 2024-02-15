package it.unical.demacs.fake_vinted_android.model

import java.time.LocalDate

data class Utente(
    var id: Long? ,
    var nome: String? ,
    var cognome: String? ,
    var email: String? ,
    var password: String? ,
    var username: String?
)