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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@Composable
fun AddItem(navHostController: NavHostController, apiService: ApiService, sessionManager: SessionManager) {
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






    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri?->
        imageUri = uri
        // Ottieni il percorso dell'immagine dalla URI
        val imageInputStream = context.contentResolver.openInputStream(uri!!)
        val imageByteArray = imageInputStream?.readBytes()

        if (imageByteArray != null) {
            base64Image.value = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
        }
    }


    LazyColumn(
        modifier = Modifier.padding(all = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Spacer(modifier = Modifier.height(90.dp))

            base64Image.value?.let { base64String ->
                val imageBitmap = remember {
                    val decodedBytes = Base64.decode(base64String.substringAfter(','), Base64.DEFAULT)
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


            //Spacer(modifier = Modifier.weight(0.2f))
            InputField(name = stringResource(R.string.item_title), reducedPaddingModifier, nameState)
            InputField(name = stringResource(R.string.item_description), reducedPaddingModifier, descriptionState)
            InputField(name = stringResource(R.string.item_price), reducedPaddingModifier, priceState)

            val CategoryOptions = listOf("Clothes","Shoes", "Accessories", "Other")
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
                    label = { Text("Category") },
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

            val ConditionOptions = listOf("New with tag","New without tag","Very good","Good","Satisfactory")
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
                    label = { Text("Condition") },
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
                    .padding(vertical = 30.dp)
                    .height(IntrinsicSize.Max),
                onClick = { launcher.launch("image/*") }
            ) {
                Text(stringResource(R.string.add_item_image))
            }


            Button(content = {
                Text(stringResource(R.string.add_item_sale))

            },
                modifier = commonModifier
                    .padding(vertical = 30.dp)
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
                                base64Image.value?:"",
                                categoria,
                                condizioni
                            )
                        } catch (e : Exception){

                        }
                    }




                }
            )
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog.value = false
                    },
                    title = {
                        Text(text = stringResource(R.string.add_item_ok))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                navHostController.navigate(Routes.FIRSTPAGE.route)
                                showDialog.value = false // Chiudi il popup
                            }
                        ) {
                            Text(text = "OK")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text(
                text = "Back Home",
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .clickable(onClick = { navHostController.navigate(Routes.FIRSTPAGE.route) })
            )
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
                    fieldState.value = newValue },
                label = { Text(name) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                modifier = modifier.padding(vertical = 5.dp)
            )
        }
        else {
            TextField(
                value = fieldState.value,
                onValueChange = { newValue ->
                    fieldState.value = newValue },
                label = { Text(name) },
                singleLine = true,
                modifier = modifier.padding(vertical = 5.dp)
            )
        }
    }
}