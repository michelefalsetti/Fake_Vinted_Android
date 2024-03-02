package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Favorites
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.Wallet
import it.unical.demacs.fake_vinted_android.viewmodels.ItemViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(itemViewModel: ItemViewModel, navController: NavHostController) {
    val items by itemViewModel.itemsInVendita.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getToken()
    val apiService = RetrofitClient.create(sessionManager,context)
    val favorites by itemViewModel.favorites.collectAsState()



    LaunchedEffect(key1 = true) {
        itemViewModel.loadFavorites()
        itemViewModel.fetchItemsInVendita()

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
                        Icon(Icons.Default.AccountCircle, contentDescription = null)}
                    // ... Altri IconButton per le altre voci della BottomAppBar ...
                }
            }
        }
    ) {innerPadding ->
        LazyColumn(

            modifier = Modifier.padding(16.dp),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())

        )
            {

            items(items) { item ->
                ItemPreview(item,navController,sessionManager,apiService, favorites)
            }
        }
    }
}

@Composable
fun ItemPreview(item: Item, navController: NavController,sessionManager: SessionManager, apiService: ApiService, favorites: Set<Favorites>) {
    var isFavorited by remember { mutableStateOf(favorites.any { it.idprodotto == item.id }) }
    val coroutineScope = rememberCoroutineScope()


    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                val itemId = item.id
                navController.navigate(Routes.ITEM.route.replace("{itemId}", itemId.toString()))
            },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            item.immagini?.let { imageUrl ->
                val imageBitmap = remember {
                    val decodedBytes = Base64.decode(imageUrl.substringAfter(','), Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                }
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Immagine Prodotto",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

            }
            Text(item.nome, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                textAlign = TextAlign.Center)

            Row(verticalAlignment = Alignment.CenterVertically) {
                val prezzoText = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Prezzo: ")
                    }
                    append("${item.prezzo}€")
                }

                Text(
                    text = prezzoText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    coroutineScope.launch {
                        val token = sessionManager.getToken()
                        val user = apiService.getCurrentUser("Bearer $token", token)
                        if (token != null) {
                            val response = if (isFavorited) {
                                // Chiama API per rimuovere dai preferiti
                                apiService.removeFromFavorites("Bearer $token", user.body()?.id, item.id)
                            } else {
                                // Chiama API per aggiungere ai preferiti
                                apiService.addFavorite("Bearer $token", user.body()?.id, item.id)
                            }

                            if (response.isSuccessful) {
                                isFavorited = !isFavorited // Aggiorna lo stato dei preferiti
                            } else {
                            }
                            }
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Preferito",
                        tint = if (isFavorited) Color.Red else Color.Gray
                    )
                }
            }}
    }
}


@Composable
fun ItemPage(itemId: Long, itemViewModel: ItemViewModel = viewModel(), navController: NavController, sessionManager: SessionManager, apiService: ApiService) {


    val item by itemViewModel.currentItem.collectAsState()
    val isLoading = itemViewModel.isLoading.collectAsState().value
    val error = itemViewModel.error.collectAsState().value

    LaunchedEffect(itemId) {
        itemViewModel.loadSingleItem(itemId)

    }


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error)
        } else item?.let {
            ItemContent(item = it, navController = navController,sessionManager = sessionManager, apiService = apiService)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemContent(item: Item, navController: NavController,sessionManager: SessionManager, apiService: ApiService ) {
    val token = sessionManager.getToken()
    val scrollState = rememberScrollState()
    var offerText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isOfferSuccessDialogOpen by remember { mutableStateOf(false) }
    var isOfferErrorDialogOpen by remember { mutableStateOf(false) }
    var ownitemerror by remember { mutableStateOf(false) }
    var lowwalleterror by remember { mutableStateOf(false) }
    var invalidoffer by remember { mutableStateOf(false) }

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        item.immagini?.let { imageUrl ->
            val imageBitmap = remember {
                val decodedBytes = Base64.decode(imageUrl.substringAfter(','), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            }
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Immagine Prodotto",
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

        }

        Text(
            text = item.nome,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        val annotatedText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Descrizione: ")
            }
            append(item.descrizione)
        }

        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp,  vertical = 10.dp)
        )

        val prezzoText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Prezzo: ")
            }
            append("${item.prezzo}€")
        }

        Text(
            text = prezzoText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp,  vertical = 10.dp)
        )

        val categoriaText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Categoria: ")
            }
            append(item.categoria)
        }

        Text(
            text = categoriaText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        val condizioniText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Condizioni: ")
            }
            append(item.condizioni)
        }

        Text(
            text = condizioniText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp,  vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        var isOfferDialogOpen by remember { mutableStateOf(false) }

        if (isOfferDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isOfferDialogOpen = false
                },
                title = {
                    Text("Fai un'offerta")
                },
                text = {
                    Column {
                        TextField(
                            value = offerText,
                            onValueChange = {

                                offerText = it
                            },
                            label = {
                                Text("Importo dell'offerta")
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {

                            val intValue = offerText.toIntOrNull()

                            if (intValue != null) {
                                val offerAmount = intValue
                                coroutineScope.launch {
                                    val saldo = apiService.getSaldo("Bearer $token", token)
                                    val user = apiService.getCurrentUser("Bearer $token", token)
                                    val prezzoconspedizione = offerAmount + 2.99

                                    if (user.body()?.id == item.idUtente) {
                                        ownitemerror = true

                                    } else if (saldo.body()?.saldo!! < prezzoconspedizione) {
                                       lowwalleterror = true
                                    } else if (offerAmount ==0 || offerAmount> item.prezzo!!){
                                        invalidoffer = true
                                    }
                                    else{
                                        val response = apiService.makeOffer(
                                            "Bearer $token",
                                            user.body()?.id,
                                            item.idUtente,
                                            item.id,
                                            offerAmount,
                                            user.body()?.username,
                                            item.nome
                                        )

                                        if (response.isSuccessful) {
                                            isOfferSuccessDialogOpen = true
                                        } else {
                                            isOfferErrorDialogOpen = true
                                        }
                                    }
                                    isOfferDialogOpen = false
                                }
                            }
                        },
                    ) {
                        Text("Invia offerta")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            isOfferDialogOpen = false
                        },
                    ) {
                        Text("Annulla")
                    }
                }
            )
        }

        if (isOfferSuccessDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isOfferSuccessDialogOpen = false
                },
                title = {
                    Text("Offerta inviata con successo")
                },
                text = {
                    Text("Se l'utente accetterà o rifiuterà l'offerta ti arriverà una notifica!" +
                            "                                                                                                   " +
                            "N.B. se l'utente accetterà l'offerta acquisterai automaticamente il prodotto!")

                },
                confirmButton = {
                    Button(
                        onClick = {
                            isOfferSuccessDialogOpen = false
                        },
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (isOfferErrorDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isOfferErrorDialogOpen = false
                },
                title = {
                    Text("Errore nell'invio dell'offerta")
                },
                text = {
                    Text("Si è verificato un errore durante l'invio dell'offerta. Riprova più tardi.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isOfferErrorDialogOpen = false
                        },
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (ownitemerror) {
            AlertDialog(
                onDismissRequest = {
                    ownitemerror = false
                },
                title = {
                    Text("Errore nell'invio dell'offerta")
                },
                text = {
                    Text("Non puoi fare offerte per i tuoi articoli!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                           ownitemerror= false
                        },
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (lowwalleterror) {
            AlertDialog(
                onDismissRequest = {
                    lowwalleterror= false
                },
                title = {
                    Text("Errore nell'invio dell'offerta")
                },
                text = {
                    Text("Saldo insufficiente, ci dispiace!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            lowwalleterror= false
                        },
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (invalidoffer) {
            AlertDialog(
                onDismissRequest = {
                   invalidoffer = false
                },
                title = {
                    Text("Offerta non valida")
                },
                text = {
                    Text("L'offerta deve essere maggiore di 0 e minore uguale al prezzo di vendita!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                           invalidoffer= false
                        },
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        Button(
            onClick = {
                isOfferDialogOpen = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Fai un'offerta")
        }

        Button(
            onClick = { navController.navigate("purchase/${item.id}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Acquista")
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PurchasePage(itemId: Long, itemViewModel: ItemViewModel = viewModel(),userViewModel: UserViewModel, apiService: ApiService,sessionManager: SessionManager,navController: NavController) {

    val saldoState by userViewModel.saldo.collectAsState()
    val item by itemViewModel.currentItem.collectAsState()
    val isLoading = itemViewModel.isLoading.collectAsState().value
    val error = itemViewModel.error.collectAsState().value



    LaunchedEffect(itemId) {
        itemViewModel.loadSingleItem(itemId)
        userViewModel.getsaldo()
        userViewModel.getCurrentUser()
    }
    saldoState?.let { Log.d("saldo", saldoState!!.saldo.toString())}


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error)
        } else item?.let {
            saldoState?.let { it1 -> PurchaseContent(item = it, wallet = it1,apiService,sessionManager, navController,userViewModel) }
        }
    }

}
@Composable
fun PurchaseContent(item: Item, wallet: Wallet, apiService: ApiService,sessionManager: SessionManager,navController: NavController,userViewModel: UserViewModel) {
    val token = sessionManager.getToken()
    val userState by userViewModel.user.collectAsState()
    val prezzoProdotto = item.prezzo // Prezzo del prodotto
    val costoSpedizione = 2.99 // Costo di spedizione fisso
    val prezzoTotale = prezzoProdotto?.plus(costoSpedizione) ?: 0.0
    val prezzoFormattato = String.format("%.2f", prezzoTotale)
    val coroutineScope = rememberCoroutineScope()
    var successDialogVisible by remember { mutableStateOf(false) }
    var insufficientBalanceDialogVisible by remember { mutableStateOf(false) }
    var purchaseownproduct by remember { mutableStateOf(false) }




    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(
            text = "Pagamento",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        item.immagini?.let { imageUrl ->
            val imageBitmap = remember {
                val decodedBytes = Base64.decode(imageUrl.substringAfter(','), Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            }
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Immagine Prodotto",
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

        }

        Text(
            text = item.nome,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 18.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )


        val prezzoText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Prezzo prodotto: ")
            }
            append("${item.prezzo}€")
        }

        Text(
            text = prezzoText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        val spedizioneText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Costo spedizione: ")
            }
            append("${costoSpedizione}€")
        }

        Text(
            text = spedizioneText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        val totaleText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Prezzo totale: ")
            }
            append("${prezzoFormattato}€")
        }

        Text(
            text = totaleText,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )




        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (wallet.saldo >= prezzoTotale!! && item.idUtente != userState?.id) {
                    coroutineScope.launch {
                        apiService.buyItem("Bearer $token",
                            userState?.username,item.id, token, prezzoTotale)

                        item.idUtente?.let {
                            apiService.addUserNotification("Bearer $token",token,
                                it,"L'utente ${userState?.username} ha acquistato il tuo prodotto ${item.nome}!" )
                        }
                        successDialogVisible = true

                    }
                }
                else {
                    insufficientBalanceDialogVisible = true
                }

                if( item.idUtente == userState?.id){
                    purchaseownproduct = true;
                }


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Paga con il Wallet",
                style = MaterialTheme.typography.titleMedium.copy( // Usa h5 come esempio per aumentare la dimensione, adattalo secondo necessità
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    if (successDialogVisible && !purchaseownproduct) {
        AlertDialog(
            onDismissRequest = {
                successDialogVisible = false
            },
            title = {
                Text(text = "Pagamento completato")
            },
            text = {
                Text(text = "Il pagamento è stato completato con successo!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        successDialogVisible = false
                        navController.navigate(Routes.FIRSTPAGE.route)
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }

    if(purchaseownproduct){
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "Impossibile acquistare")
            },
            text = {
                Text(text = "Non puoi acquistare i tuoi stessi prodotti!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.navigate(Routes.FIRSTPAGE.route)

                    }
                ) {
                    Text(text = "OK")
                }

            }
        )
    }


    if (insufficientBalanceDialogVisible && !purchaseownproduct) {
        AlertDialog(
            onDismissRequest = {
                insufficientBalanceDialogVisible = false
            },
            title = {
                Text(text = "Saldo insufficiente")
            },
            text = {
                Text(text = "Il tuo saldo non è sufficiente per effettuare il pagamento.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        insufficientBalanceDialogVisible = false

                    }
                ) {
                    Text(text = "OK")
                }

            }
        )

    }
}






