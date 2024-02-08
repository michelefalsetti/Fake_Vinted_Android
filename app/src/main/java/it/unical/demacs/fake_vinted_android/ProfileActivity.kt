package it.unical.demacs.fake_vinted_android

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import it.unical.demacs.fake_vinted_android.viewmodels.UserState
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel


@Composable
fun ProfilePage(userViewModel: UserViewModel) {
    val userState by userViewModel.userState.collectAsState()
    var editingMode by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf(userState.username) }
    var tempFirstName by remember { mutableStateOf(userState.firstName) }
    var tempLastName by remember { mutableStateOf(userState.lastName) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Immagine del profilo
        ProfileImage(userState.profileImageUrl, userViewModel) { newImageUri: Uri? ->
            // Qui puoi aggiornare il ViewModel con il nuovo URI dell'immagine
            if (newImageUri != null) {
                userViewModel.updateProfileImage(newImageUri)
            }
        }


        Spacer(modifier = Modifier.height(90.dp))

        // Modifica e visualizzazione di Username, Nome e Cognome
        if (editingMode) {
            EditableUserInfo(tempUsername, tempFirstName, tempLastName) { newUsername, newFirstName, newLastName ->
                tempUsername = newUsername
                tempFirstName = newFirstName
                tempLastName = newLastName
            }
        } else {
            DisplayUserInfo(userState)
        }

        Button(onClick = {
            if (editingMode) {
                // Salva le modifiche nel ViewModel
                userViewModel.updateUserDetails(tempUsername, tempFirstName, tempLastName)
            }
            editingMode = !editingMode
        }) {
            Text(if (editingMode) "Salva" else "Modifica")
        }


        Spacer(modifier = Modifier.height(10.dp))



        // ... ulteriori dettagli e componenti ...

        Spacer(modifier = Modifier.weight(1f)) // Assicura che il contenuto non sia tagliato
    }




}
@Composable
fun ProfileImage(imageUrl: String, userViewModel: UserViewModel, param: (Uri?) -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri?->
        imageUri = uri
        userViewModel.onImageSelected(uri)
        // Ottieni il percorso dell'immagine dalla URI
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Immagine del profilo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        } else {
            // Mostra un placeholder se non c'è un'immagine
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Cambia Immagine")
        }
    }
}


@Composable
fun DisplayUserInfo(userState: UserState) {

    Text(
        "Username: ${userState.username}",
        style = androidx.compose.ui.text.TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Visualizzazione Nome
    Text(
        "Nome: ${userState.firstName}",
        style = androidx.compose.ui.text.TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Visualizzazione Cognome
    Text(
        "Cognome: ${userState.lastName}",
        style = androidx.compose.ui.text.TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Visualizzazione Email
    Text(
        "Email: ${userState.email}",
        style = androidx.compose.ui.text.TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
    )
    Spacer(modifier = Modifier.height(8.dp))

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableUserInfo(username: String, firstName: String, lastName: String, onValueChange: (String, String, String) -> Unit) {

    TextField(
        value = username,
        onValueChange = { newUsername -> onValueChange(newUsername, firstName, lastName) },
        label = { Text("Username") }
    )
    TextField(
        value = firstName,
        onValueChange = { newFirstName -> onValueChange(username, newFirstName, lastName) },
        label = { Text("Nome") }
    )
    TextField(
        value = lastName,
        onValueChange = { newLastName -> onValueChange(username, firstName, newLastName) },
        label = { Text("Cognome") }
    )
}


