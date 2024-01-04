package it.unical.demacs.fake_vinted_android
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.unical.demacs.fake_vinted_android.ui.theme.Fake_Vinted_AndroidTheme
class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fake_Vinted_AndroidTheme {
                var showLoginPage by remember { mutableStateOf(true) }

                if (showLoginPage) {
                    LoginPage(onSwitchToRegister = { showLoginPage = false })
                } else {
                    RegisterPage(onSwitchToLogin = { showLoginPage = true })
                }            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(onSwitchToLogin: () -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showNicknameError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registrati", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = {
                nickname = it
                showNicknameError = it.length < 4
            },
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth(),
            isError = showNicknameError
        )
        if (showNicknameError) {
            Text("Il nickname deve avere almeno 4 caratteri", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Conferma Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: valida i campi di input
                           TODO: comunica con il backend per registrare l'utente
                           TODO: gestisci la risposta del backend
                           */ if (nickname.length >= 4) {
                // Procedi con la registrazione
            }},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrati")
        }

        Button(onClick = onSwitchToLogin) {
            Text("Hai già un account? Accedi")
        }



    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(onSwitchToRegister: () -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Accedi", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (loginError) {
            Text("Login fallito. Riprova.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Qui inserisci la logica per autenticare l'utente
                // Potrebbe coinvolgere la comunicazione con un backend/server
                // Imposta loginError su true se il login fallisce
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Accedi")
        }

        Button(onClick = onSwitchToRegister) {
            Text("Non hai un account? Registrati")
        }
    }
}