@file:Suppress("DEPRECATION")

package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale.Category
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.SparseArray
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddItem(navHostController: NavHostController, apiService: ApiService, sessionManager: SessionManager, navController: NavController) {
    val commonModifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)

    val token = sessionManager.getToken()

    val coroutineScope = rememberCoroutineScope()
    val nameState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val priceState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val base64Image = remember { mutableStateOf<String?>(null) }

    val reducedPaddingModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 5.dp) // Riduci il padding verticale

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        // Ottieni il percorso dell'immagine dalla URI
        val imageInputStream = context.contentResolver.openInputStream(uri!!)
        val imageByteArray = imageInputStream?.readBytes()

        if (imageByteArray != null) {
            base64Image.value = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
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
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(all = 3.dp)
        ) {
            item {

                base64Image.value?.let { base64String ->
                    val imageBitmap = remember {
                        val decodedBytes =
                            Base64.decode(base64String.substringAfter(','), Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    }
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(400.dp)
                            .padding(20.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Spazio tra i campi di input e il primo Button

                InputField(
                    name = stringResource(R.string.item_title),
                    reducedPaddingModifier,
                    nameState
                )
                InputField(
                    name = stringResource(R.string.item_description),
                    reducedPaddingModifier,
                    descriptionState
                )
                InputField(
                    name = stringResource(R.string.item_price),
                    reducedPaddingModifier,
                    priceState
                )

                val CategoryOptions = listOf("Vestiti", "Scarpe", "Accessori", "Altro")
                var expanded by remember { mutableStateOf(false) }
                var selectedOptionText by remember { mutableStateOf("") }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .menuAnchor(),
                        readOnly = true,
                        value = selectedOptionText,
                        onValueChange = {},
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        CategoryOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedOptionText = selectionOption
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                val ConditionOptions =
                    listOf("Nuovo con cartellino", "Nuovo senza cartellino", "Ottime", "Buone", "Discrete")
                var expanded1 by remember { mutableStateOf(false) }
                var selectedOptionText1 by remember { mutableStateOf("") }

                ExposedDropdownMenuBox(
                    expanded = expanded1,
                    onExpandedChange = { expanded1 = !expanded },
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .menuAnchor(),
                        readOnly = true,
                        value = selectedOptionText1,
                        onValueChange = {},
                        label = { Text("Condizioni") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded1,
                        onDismissRequest = { expanded = false },
                    ) {
                        ConditionOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedOptionText1 = selectionOption
                                    expanded1 = false

                                },
                            )
                        }
                    }
                }

                Button(
                    modifier = commonModifier
                        .padding(vertical = 5.dp)
                        .height(IntrinsicSize.Max),
                    onClick = { launcher.launch("image/*") }
                ) {
                    Text(text = "Carica immagine")
                }

                Button(
                    modifier = commonModifier
                        .padding(vertical = 8.dp)
                        .height(IntrinsicSize.Max),
                    onClick = {
                        val nome = nameState.value
                        val descrizione = descriptionState.value
                        val price = priceState.value.toBigDecimal()
                        val categoria = selectedOptionText
                        val condizioni = selectedOptionText1
                        coroutineScope.launch {
                            try {
                                showDialog.value = true
                                val response = apiService.addItem(
                                    "Bearer $token",
                                    token,
                                    nome,
                                    descrizione,
                                    price,
                                    base64Image.value ?: "",
                                    categoria,
                                    condizioni
                                )
                            } catch (e: Exception) {

                            }
                        }
                    }
                ) {
                    Text(text = "Metti in vendita")
                }
                Spacer(modifier = Modifier.padding(50.dp))

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(name: String, modifier: Modifier, fieldState: MutableState<String>) {
    val passName = stringResource(R.string.login_password)

    Row(horizontalArrangement = Arrangement.Center) {
        if (name == passName) { // ATTENZIONE!!!!! MODIFICARE QUANDO IMPLEMENTIAMO LOGIN
            TextField(
                value = fieldState.value,
                onValueChange = { newValue ->
                    fieldState.value = newValue
                },
                label = { Text(name) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                modifier = modifier.padding(vertical = 5.dp)
            )
        } else {
            TextField(
                value = fieldState.value,
                onValueChange = { newValue ->
                    fieldState.value = newValue
                },
                label = { Text(name) },
                singleLine = true,
                modifier = modifier.padding(vertical = 5.dp)
            )
        }
    }
}
