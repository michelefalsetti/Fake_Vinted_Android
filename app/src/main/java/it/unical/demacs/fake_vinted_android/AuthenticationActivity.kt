package it.unical.demacs.fake_vinted_android
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.ui.theme.Fake_Vinted_AndroidTheme
import it.unical.demacs.fake_vinted_android.viewmodels.AddressFormViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserFormViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage( userFormViewModel: UserFormViewModel, navHostController: NavHostController, apiService: ApiService) {
    val coroutineScope = rememberCoroutineScope()
    val userState by userFormViewModel.userState.collectAsState()
    var showNicknameError by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

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
            value = userState.username,
            onValueChange = {userFormViewModel.updateUsername(it)},
            label = { Text("Nickname") },
            singleLine = true,
        )
        if (showNicknameError) {
            Text("Il nickname deve avere almeno 4 caratteri", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = userState.email,
            onValueChange = { userFormViewModel.updateEmail(it) },
            label = { Text(stringResource(R.string.user_email)) },
            singleLine = true,
        )

        OutlinedTextField(
            value = userState.firstName,
            onValueChange = { userFormViewModel.updateFirstName(it) },
            label = { Text(stringResource(R.string.user_firstName)) },
            singleLine = true,

        )

        OutlinedTextField(
            value = userState.lastName,
            onValueChange = { userFormViewModel.updateLastName(it) },
            label = { Text(stringResource(R.string.user_lastName)) },
            singleLine = true
        )

        OutlinedTextField(
            value = userState.password,
            onValueChange = {
                userFormViewModel.updatePassword(it)
            },
            label = { Text(stringResource(R.string.user_password)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        )
        OutlinedTextField(
            value = userState.passwordConfirm,
            onValueChange = {
                userFormViewModel.updatePasswordConfirm(
                    it,
                    userState.password
                )
            },
            label = { Text(stringResource(R.string.user_password_confirm)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val username = userState.username
                val password = userState.password
                val email = userState.email
                val nome = userState.lastName
                val cognome = userState.firstName

                coroutineScope.launch {
                    try {
                        showDialog.value= true
                        val response=  apiService.register(username, password, email, nome, cognome)
                    } catch ( e : Exception){}
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrati")
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                },
                title = {
                    Text(text = "Utente registrato")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            navHostController.navigate(Routes.LOGIN.route)
                            showDialog.value = false // Chiudi il popup
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(onClick = { navHostController.navigate(Routes.LOGIN.route) }) {
            Text("Hai già un account? Accedi")
        }



    }
}

@Composable
fun LoginPage(navController: NavController, apiService: ApiService, sessionManager: SessionManager, isLogged : MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val nicknameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Accedi", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        InputField(name = stringResource(R.string.login_username),modifier = Modifier, nicknameState)
        InputField(name = stringResource(R.string.login_password), modifier = Modifier, passwordState)


        if (loginError) {
            Text("Login fallito. Riprova.", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val username = nicknameState.value
                val password = passwordState.value

                coroutineScope.launch {
                    try {
                        sessionManager.clearToken()
                        sessionManager.clearUsername()
                        val response = apiService.authenticate(username, password)
                        if(response.isSuccessful){
                            sessionManager.saveUsername(username)
                            isLogged.value = true
                            navController.navigate(Routes.FIRSTPAGE.route)
                        }

                    } catch (e: Exception) {
                        // Si è verificato un errore durante la chiamata API
                    }
            }
            }
        ) {
            Text("Accedi")
        }
        Button(onClick = { navController.navigate(Routes.REGISTER.route) }) {
            Text("Non hai un account? Registrati")
        }

    }
}