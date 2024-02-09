package it.unical.demacs.fake_vinted_android

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel


@Composable
fun ProfilePage(userViewModel: UserViewModel) {
    val userState by userViewModel.user.collectAsState()
    val saldoState by userViewModel.saldo.collectAsState()

    val user = userState

    LaunchedEffect(key1 = true) {
        userViewModel.getCurrentUser()
        userViewModel.getsaldo()
    }


    // Supponendo che user possa essere null (UtenteDTO?):
    if (user != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon), // Assicurati che l'icona sia presente nelle tue risorse
                contentDescription = "icona",
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.Transparent)
            )

            Spacer(modifier = Modifier.height(90.dp))

            // Chiama la funzione DisplayUserInfo per mostrare le informazioni dell'utente
            saldoState?.let { DisplayUserInfo(user = user, saldo = it) }
        }
    }

}



@Composable
fun DisplayUserInfo(user: UtenteDTO, saldo: Wallet ) {
    val usernameText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Username: ")
        }
        append(user.username)
    }

    val nomeText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Nome: ")
        }
        append(user.nome)
    }

    val cognomeText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Cognome: ")
        }
        append(user.cognome)
    }

    val emailText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Email: ")
        }
        append(user.email)
    }
    val saldoText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Saldo: ")
        }
        // Utilizza il saldo del Wallet se non è null, altrimenti mostra "Non disponibile"
        append(if (saldo != null) "${saldo.saldo} €" else "Non disponibile")
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = saldoText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 18.dp)
        )

        Text(
            text = usernameText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = nomeText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = cognomeText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = emailText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }
}




