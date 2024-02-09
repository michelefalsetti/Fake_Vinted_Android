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
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle


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
                    IconButton(onClick = { navController.navigate(Routes.SEARCH.route) }) {
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
            Text(item.nome, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                textAlign = TextAlign.Center)

            val prezzoText = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Prezzo: ")
                }
                append("${item.prezzo}€")
            }

            Text(
                text = prezzoText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 12.dp)

            )
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


    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error)
        } else item?.let {
            ItemContent(item = it)
        }
    }
}

@Composable
fun ItemContent(item: Item) {
    val scrollState = rememberScrollState()

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
            modifier = Modifier.padding(horizontal = 16.dp,  vertical = 18.dp).fillMaxWidth(),
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

        Button(
            onClick = { /* Implementa l'azione al click */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Acquista")
        }
    }
}

