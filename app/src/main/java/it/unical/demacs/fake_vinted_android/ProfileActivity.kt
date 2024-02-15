package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.wait


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(userViewModel: UserViewModel,navController: NavController, apiService: ApiService,sessionManager: SessionManager) {
    val userState by userViewModel.user.collectAsState()
    val saldoState by userViewModel.saldo.collectAsState()

    val user = userState

    LaunchedEffect(key1 = true) {
        userViewModel.getCurrentUser()
        userViewModel.getsaldo()
    }


    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate(Routes.FIRSTPAGE.route) }) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    }
                    IconButton(onClick = { navController.navigate(Routes.SEARCH.route) }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                    IconButton(onClick = { navController.navigate(Routes.ADDITEM.route) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                    IconButton(onClick = { navController.navigate(Routes.NOTIFICATION.route) }) {
                        Icon(
                            Icons.Default.Email, contentDescription = null,
                        )
                    }
                    IconButton(onClick = { navController.navigate(Routes.PROFILE.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                saldoState?.let { DisplayUserInfo(user = user, saldo = it,apiService,sessionManager,navController) }
            }
        }
    }

}



@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun DisplayUserInfo(user: UtenteDTO, saldo: Wallet , apiService: ApiService,sessionManager: SessionManager,navController: NavController) {
    val token = sessionManager.getToken()
    val itemsAcquistati= remember { mutableListOf<Item>() }
    val showResult = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            if (token != null) {
                val res = apiService.getItemAcquistati("Bearer $token", token)
                if (res.isSuccessful) {
                    for (item in res.body()!!) {
                        Log.d("oggetti", item.toString())
                        itemsAcquistati.add(item)
                    }
                    showResult.value = true
                }

            }
        }
    }


    val usernameText = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(user.username)
        }
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
        val saldo1 = saldo.saldo
        val formato = "%.2f"
        val risultatoFormattato = String.format(formato, saldo1)
        // Utilizza il saldo del Wallet se non è null, altrimenti mostra "Non disponibile"
        append(if (saldo != null) "${risultatoFormattato} €" else "Non disponibile")
    }

    Column(modifier = Modifier.padding(5.dp)) {
        Card {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth() // Fai espandere la Row su tutta la larghezza
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "icona",
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Transparent)
                )
                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth() // Fai espandere la Column su tutta la larghezza
                ) {
                    Text(
                        text = usernameText,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Text(
                        text = saldoText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = 4.dp)

                    )
                }
            }
        }
    }

        Spacer(modifier = Modifier.height(20.dp)) // Regola questo valore per modificare lo spazio verticale


    DatiButton {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = nomeText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = cognomeText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = emailText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )

            }
        }
    }
    StoricoOrdiniButton {

        if (showResult.value) {
            if (itemsAcquistati.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(itemsAcquistati) { item ->
                        ItemInVenditaPreview(
                            item,
                            navController
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Nessun ordine effettuato!")
                }
            }
        }
    }
}



@Composable
fun DatiButton(content: @Composable (isEspanso: Boolean) -> Unit) {
    var isEspanso by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { isEspanso = !isEspanso },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
            Text(if (isEspanso) "Dati Personali ⌄" else "Dati Personali >")
        }

        if (isEspanso) {
            content(isEspanso)
        }
    }
}

@Composable
fun StoricoOrdiniButton(content: @Composable (isEspanso: Boolean) -> Unit) {
    var isEspanso by remember { mutableStateOf(false) }

    Column {
        Button(onClick = { isEspanso = !isEspanso },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
            Text(if (isEspanso) "Storico Ordini⌄" else "Storico Ordini >")
        }

        if (isEspanso) {
            content(isEspanso)
        }
    }
}

@Composable
fun ItemInVenditaPreview(item: Item, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            item.immagini?.let { imageUrl ->
                val imageBitmap = remember {
                    val decodedBytes = Base64.decode(imageUrl.substringAfter(','), Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                }
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Immagine Prodotto",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Aggiunge uno spazio tra l'immagine e il testo

            Column(
                modifier = Modifier
                    .weight(1f) // Fai espandere la colonna per riempire lo spazio disponibile
                    .fillMaxHeight()
            ) {
                Text(item.nome, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))

                val prezzoText = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Prezzo: ")
                    }
                    append("${item.prezzo}€")
                }

                Text(
                    text = prezzoText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Aggiungi altre informazioni sull'articolo se necessario
            }
        }
    }
}
