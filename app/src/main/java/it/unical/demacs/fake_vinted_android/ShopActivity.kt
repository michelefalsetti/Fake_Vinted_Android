package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.viewmodels.ItemViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(itemViewModel: ItemViewModel, navController: NavHostController) {
    val items by itemViewModel.itemsInVendita.collectAsState()

    LaunchedEffect(key1 = true) {
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
                    IconButton(onClick = { /* TODO: Gestire la navigazione per la ricerca */ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                    IconButton(onClick = { navController.navigate(Routes.ADDITEM.route) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Email, contentDescription = null,
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
                ItemPreview(item,navController)
            }
        }
    }
}
@Composable
fun ItemPreview(item: Item, navController: NavController) {
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
                Image(
                    painter = rememberAsyncImagePainter(model = item.immagini),
                    contentDescription = "Immagine Prodotto",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Text(item.nome, style = MaterialTheme.typography.titleMedium)
            Text("Prezzo: ${item.prezzo}€", style = MaterialTheme.typography.bodyMedium)
            // Aggiungi altre informazioni sull'articolo se necessario
        }
    }
}


@Composable
fun ItemPage(itemId: Long, itemViewModel: ItemViewModel = viewModel()) {


    val item by itemViewModel.currentItem.collectAsState()
    val isLoading = itemViewModel.isLoading.collectAsState().value
    val error = itemViewModel.error.collectAsState().value

    LaunchedEffect(itemId) {
        itemViewModel.loadSingleItem(itemId)
    }


    if (isLoading) {
        CircularProgressIndicator()
    } else if (error != null) {
        Text(text = error)
    } else item?.let { ItemContent(item = it) }
}

@Composable
fun ItemContent(item: Item) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.verticalScroll(scrollState)) {
        item.immagini?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Immagine Prodotto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        Text(
            text = item.nome,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = item.descrizione ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "Prezzo: ${item.prezzo}€",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Categoria: ${item.categoria}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Condizioni: ${item.condizioni}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Implementa l'azione al click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Acquista")
        }

        // Continua ad aggiungere gli altri dettagli dell'item come prima
    }
}

