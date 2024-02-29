package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Notifications
import it.unical.demacs.fake_vinted_android.model.Offer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPage(apiService: ApiService, sessionManager: SessionManager,navController: NavController) {
    var notificationResult by remember { mutableStateOf<List<Notifications>>(emptyList()) }
    val token = sessionManager.getToken()

    LaunchedEffect(token) {
        if (notificationResult.isEmpty()) {
            val res = apiService.getUserNotification("Bearer $token", token)
            notificationResult = res.body() ?: emptyList()
        }
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
    ) { innerpadding ->
        Scaffold(topBar =
        {
            TopAppBar(
                title = { },
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(
                            text = "Notifiche",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.NOTIFICATION.route)
                                }
                                .align(Alignment.CenterVertically)
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )


                        Spacer(
                            modifier = Modifier
                                .background(Color.Gray)
                                .width(2.dp)
                                .height(30.dp)
                                .fillMaxHeight()
                        )


                        Text(
                            text = "Offerte",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.OFFER.route)
                                }
                                .align(Alignment.CenterVertically) // Centra il testo verticalmente
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center  // Centra il testo orizzontalmente
                        )
                    }
                },
            )
        }
        ) { innerpadding ->
            Column {
                Spacer(modifier = Modifier.height(10.dp))


                if (notificationResult.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = innerpadding.calculateBottomPadding(),top = innerpadding.calculateTopPadding()),
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        items(notificationResult) { notification ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),

                                elevation = CardDefaults.cardElevation()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    notification.messaggio?.let { it1 ->
                                        Text(
                                            text = it1,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Nessuna notifica da visionare!")
                    }
                }
            }
        }
    }

}




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferPage(apiService: ApiService, sessionManager: SessionManager,navController: NavController) {
    val token = sessionManager.getToken()
    var offerResult by remember { mutableStateOf<List<Offer>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(token) {
        if (offerResult.isEmpty()) {
            val user = apiService.getCurrentUser("Bearer $token", token)
            val res = user.body()?.let { apiService.getOffersByUserId("Bearer $token", it.id) }
            if (res != null) {
                offerResult = res.body() ?: emptyList()
            }
            if (res != null) {
                Log.d("offerte", res.body().toString())
            }
        }
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
    ) { innerpadding ->
        Scaffold(topBar =
        {
            TopAppBar(
                title = { },
                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(
                            text = "Notifiche",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.NOTIFICATION.route)
                                }
                                .align(Alignment.CenterVertically)
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )


                        Spacer(
                            modifier = Modifier
                                .background(Color.Gray)
                                .width(2.dp)
                                .height(30.dp)
                                .fillMaxHeight()
                        )


                        Text(
                            text = "Offerte",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate(Routes.OFFER.route)
                                }
                                .align(Alignment.CenterVertically) // Centra il testo verticalmente
                                .padding(8.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center  // Centra il testo orizzontalmente
                        )
                    }
                },
            )
        }
        ) { innerpadding ->
            Column {
                Spacer(modifier = Modifier.height(10.dp))

                var deletedOfferId by remember { mutableStateOf<Long?>(null) }

                if (offerResult.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            bottom = innerpadding.calculateBottomPadding(),
                            top = innerpadding.calculateTopPadding()
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(offerResult) { offer ->
                            if (offer.idofferta?.toLong() != deletedOfferId) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    elevation = CardDefaults.cardElevation()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "L'utente ${offer.usernameutenteofferente} ha offerto ${offer.offerta}€ per l'articolo ${offer.nomeprodotto}"
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        offer.idofferente?.let {
                                                            apiService.addUserNotification(
                                                                "Bearer $token",
                                                                token,
                                                                it,
                                                                "la tua offerta è stata accettata, hai acquistato l'articolo ${offer.nomeprodotto}!"
                                                            )
                                                        }

                                                        offer.idproprietario?.let {
                                                            apiService.addUserNotification("Bearer $token",token,
                                                                it,"L'utente ${offer.usernameutenteofferente} ha acquistato il tuo prodotto ${offer.nomeprodotto}!" )
                                                        }
                                                        val prezzototale = offer.offerta?.plus(2.99)
                                                        apiService.buyItem(
                                                            "Bearer $token",
                                                            offer.usernameutenteofferente,
                                                            offer.idprodotto,
                                                            token,
                                                            prezzototale
                                                        )
                                                        offer.idprodotto?.let {
                                                            apiService.deleteOffer(
                                                                "Bearer $token",
                                                                it
                                                            )
                                                            deletedOfferId =
                                                                it
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 4.dp),
                                                contentPadding = PaddingValues(8.dp),
                                            ) {
                                                Text(text = "Accetta", fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        offer.idofferente?.let {
                                                            apiService.addUserNotification(
                                                                "Bearer $token",
                                                                token,
                                                                it,
                                                                "la tua offerta per l'articolo ${offer.nomeprodotto} è stata rifiutata, ci dispiace!"
                                                            )
                                                        }
                                                        offer.idprodotto?.let {
                                                            apiService.deleteOffer(
                                                                "Bearer $token",
                                                                it
                                                            )
                                                            deletedOfferId =
                                                                it
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 4.dp),
                                                contentPadding = PaddingValues(8.dp),
                                            ) {
                                                Text(text = "Rifiuta", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Nessuna offerta arrivata!")
                    }
                }
            }
        }
    }
}




