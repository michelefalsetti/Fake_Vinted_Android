package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.NavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.UtenteDTO
import it.unical.demacs.fake_vinted_android.model.Wallet
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(userViewModel: UserViewModel,navController: NavController, apiService: ApiService,sessionManager: SessionManager) {
    val userState by userViewModel.user.collectAsState()
    val saldoState by userViewModel.saldo.collectAsState()

    val user = userState

    val isDarkTheme = remember { mutableStateOf(sessionManager.isDarkTheme()) }
    val toggleTheme = {
        val newThemeState = !isDarkTheme.value
        isDarkTheme.value = newThemeState
        sessionManager.saveThemePreference(newThemeState)
    }

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
                saldoState?.let { DisplayUserInfo(user = user, saldo = it,apiService,sessionManager, navController = navController, isDarkTheme = isDarkTheme,
                    toggleTheme = toggleTheme ) }
            }
        }
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun DisplayUserInfo(user: UtenteDTO, saldo: Wallet, apiService: ApiService, sessionManager: SessionManager, navController: NavController, isDarkTheme: MutableState<Boolean>, toggleTheme: () -> Unit ) {
    val token = sessionManager.getToken()
    val itemsAcquistati = remember { mutableListOf<Item>() }
    val itemsPreferiti = remember { mutableStateOf<List<Item>>(emptyList()) }
    val showResult = remember { mutableStateOf(false) }
    var indirizzoText by remember { mutableStateOf<AnnotatedString?>(null) }


    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            if (token != null) {
                val user = apiService.getCurrentUser("Bearer $token", token)
                val res = apiService.getItemAcquistati("Bearer $token", token)
                Log.d("itemacquistati",res.body().toString())
                val ress = apiService.getFavorites("Bearer $token", user.body()?.id)
                if (ress.isSuccessful) {
                    val favorites = ress.body() ?: emptyList()
                    val items = mutableListOf<Item>()
                    for (favorite in favorites) {
                        val itemResponse = apiService.getItem("Bearer $token", favorite.idprodotto)
                        if (itemResponse.isSuccessful) {
                            itemResponse.body()?.let { items.add(it) }
                        }
                    }
                    itemsPreferiti.value = items
                }
                val indirizzo = apiService.getIndirizzo("Bearer $token", token)
                if (ress.isSuccessful && res.isSuccessful && indirizzo.isSuccessful) {

                    val via = indirizzo.body()?.via ?: ""
                    val numerocivico = indirizzo.body()?.numerocivico ?: ""
                    val cap = indirizzo.body()?.cap ?: ""
                    val citta = indirizzo.body()?.citta ?: ""
                    val provincia = indirizzo.body()?.provincia ?: ""

                    val indirizzoCompleto = buildAnnotatedString {

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Via e Numero Civico: ")
                        }
                        append("$via, $numerocivico\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("CAP: ")
                        }
                        append("$cap\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Città e Provincia: ")
                        }
                        append("$citta, $provincia")
                    }

                    indirizzoText = indirizzoCompleto
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
        append(if (saldo != null) "${risultatoFormattato} €" else "Non disponibile")
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(5.dp)
    ) {
        Card {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()             ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "icona",
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Transparent)
                )
                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()                 ) {
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

    Spacer(modifier = Modifier.height(20.dp))
    var datiEspanso by remember { mutableStateOf(false) }
    var indirizzoEspanso by remember { mutableStateOf(false) }
    var storicoEspanso by remember { mutableStateOf(false) }
    var preferitiEspanso by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),             horizontalArrangement = Arrangement.End // Allinea il contenuto alla fine (destra)
        ) {
            ThemeSwitchButton(isDarkTheme = isDarkTheme, toggleTheme = toggleTheme)
        }
    }
    Column {
        Button(
            onClick = { datiEspanso = !datiEspanso
                storicoEspanso = false
                preferitiEspanso = false
                indirizzoEspanso = false},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(if (datiEspanso) "Dati Personali ⌄" else "Dati Personali >")
        }

        if (datiEspanso) {
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

                    Spacer(modifier = Modifier.height(15.dp))


                }
            }
        }
    }

        Column {
            Button(onClick = { indirizzoEspanso = !indirizzoEspanso
                datiEspanso = false
                preferitiEspanso = false
                storicoEspanso = false},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                Text(if (indirizzoEspanso) "Indirizzo ⌄" else "Indirizzo >")
            }

            if (indirizzoEspanso) {
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

                        indirizzoText?.let { it1 ->
                            Text(
                                text = it1,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxWidth()
                            )
                        }

                    }
                }
            }
        }



        Column {
            Button(onClick = { storicoEspanso = !storicoEspanso
                datiEspanso = false
                preferitiEspanso = false
                indirizzoEspanso = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                Text(if (storicoEspanso) "Storico Ordini⌄" else "Storico Ordini >")
            }

            if (storicoEspanso) {
                if (showResult.value) {
                    if (itemsAcquistati.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(itemsAcquistati) { item ->
                                ItemAcquistatiPreview(
                                    item
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


    Column {
        Button(onClick = { preferitiEspanso = !preferitiEspanso
            datiEspanso = false
            storicoEspanso = false
            indirizzoEspanso = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
            Text(if (preferitiEspanso) "Prodotti Preferiti⌄" else "Prodotti Preferiti >")
        }

        if (preferitiEspanso) {
            if (showResult.value) {
                if (itemsPreferiti.value.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(itemsPreferiti.value) { item ->
                            ItemPreferitiPreview(
                                item
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Nessun prodotto preferito!")
                    }
                }
            }
        }
    }

    var isDialogVisible by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var showThanksDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { isDialogVisible = true },
            modifier = Modifier.padding(8.dp)
                .fillMaxWidth()
        ) {
            Text("Lasciaci il tuo feedback!")
        }

        if (isDialogVisible) {
            AlertDialog(
                onDismissRequest = {
                    isDialogVisible = false
                },
                text = {
                    Column {
                        Text("Inserisci il tuo feedback")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (feedbackText.isNotBlank()) {
                                        coroutineScope.launch {
                                            apiService.addFeedback("Bearer $token", user.id, feedbackText)
                                            showThanksDialog = true
                                        }
                                        isDialogVisible = false
                                    } else {
                                        showErrorDialog = true
                                    }
                                }
                            )
                        )
                    }
                },
                confirmButton = {
                    IconButton(
                        onClick = {
                            if (feedbackText.isNotBlank()) {
                                coroutineScope.launch {
                                    apiService.addFeedback("Bearer $token", user.id, feedbackText)
                                    showThanksDialog = true
                                }
                                isDialogVisible = false
                            } else {
                                showErrorDialog = true
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                    }
                },
                dismissButton = {
                    IconButton(
                        onClick = {
                            isDialogVisible = false
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = {
                    showErrorDialog = false
                },
                title = {
                    Text("Errore")
                },
                text = {
                    Text("Il campo di feedback non può essere vuoto.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showErrorDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (showThanksDialog) {
            AlertDialog(
                onDismissRequest = {
                    showThanksDialog = false
                },
                title = {
                    Text("Grazie per il tuo feedback!")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showThanksDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }




    Spacer(modifier = Modifier.padding(30.dp))
    Button(onClick = { sessionManager.logout()
        navController.navigate(Routes.HOME.route)}) {
        Text( "Effettua il logout")



    }
}




@Composable
fun ItemAcquistatiPreview(item: Item) {
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

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
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
            }
        }

    }
}

@Composable
fun ItemPreferitiPreview(item: Item) {

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

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
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
            }
        }

    }
}
@Composable
fun ThemeSwitchButton(isDarkTheme: MutableState<Boolean>, toggleTheme: () -> Unit) {
    // Dimensioni per l'interruttore tema
    val width = 100.dp
    val height = 50.dp
    val toggleSize = 40.dp

    // Calcolo dell'offset basato sullo stato del tema
    val offsetX = animateDpAsState(
        targetValue = if (isDarkTheme.value) width - toggleSize else 0.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(50)) // Rende i bordi arrotondati
            .background(
                brush = if (isDarkTheme.value) {
                    Brush.horizontalGradient(
                        colors = listOf(Color.DarkGray, Color.Black)
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color.Yellow, Color(0xFFFFA500)) // Colori del sole
                    )
                }
            )
            .clickable { toggleTheme() }, // Alterna il tema al clic
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX.value)
                .size(toggleSize)
                .clip(CircleShape)
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = if (isDarkTheme.value) Alignment.Center else Alignment.CenterStart
        ) {
            Icon(
                painter = if (isDarkTheme.value) painterResource(id = R.drawable.ic_moon) else painterResource(id = R.drawable.ic_sun),
                contentDescription = "Toggle Theme",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified // Mantieni il colore dell'icona originale
            )
        }
    }
}

