package it.unical.demacs.fake_vinted_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.ui.theme.Fake_Vinted_AndroidTheme
import it.unical.demacs.fake_vinted_android.viewmodels.ItemViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserFormViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fake_Vinted_AndroidTheme {
                val isLogged = remember { mutableStateOf(false) }
                val navController = rememberNavController()
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val apiService = RetrofitClient.create(sessionManager)
                val userViewModel = UserViewModel(context)
                val userFormViewModel = UserFormViewModel()
                val itemViewModel = ItemViewModel(context)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationView(
                        userViewModel = userViewModel,
                        userFormVIewModel =userFormViewModel ,
                        apiService = apiService,
                        sessionManager = sessionManager,
                        navController = navController,
                        isLogged = isLogged,
                        itemViewModel = itemViewModel

                    )
                }

            }
        }
    }
}


@Composable
fun MainPage( navController: NavHostController) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.padding(vertical = 90.dp))
            Text(
                fontFamily = FontFamily.Monospace,
                text = "Per continuare ad usare l'app è necessario eseguire l'accesso.",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))

            Button(onClick = { navController.navigate((Routes.LOGIN.route)) }) {
                Text(text = "        Accedi        ")

            }
        }

    }


@Composable
fun NavigationView(itemViewModel: ItemViewModel, userViewModel: UserViewModel,userFormVIewModel : UserFormViewModel, apiService: ApiService, sessionManager: SessionManager, navController: NavHostController, isLogged: MutableState<Boolean>) {
    LaunchedEffect(key1 = true) {
        itemViewModel.fetchItemsInVendita()
    }
    NavHost(navController = navController, startDestination = Routes.HOME.route) {
        composable(Routes.HOME.route) {
            MainPage( navController = navController)
        }

        composable(Routes.LOGIN.route) {
            LoginPage(
                navController = navController,
                apiService = apiService ,
                sessionManager = sessionManager,
                isLogged = isLogged
            )
        }

        composable(Routes.REGISTER.route){
            RegisterPage(
                userFormViewModel = userFormVIewModel,
                navHostController = navController,
                apiService = apiService
            )
        }



        composable(Routes.ADDITEM.route) {
            AddItem(navController,apiService,sessionManager) // La composable per aggiungere un articolo
        }
        composable(Routes.PROFILE.route) {
            ProfilePage(userViewModel = userViewModel)
        }
        composable(Routes.FIRSTPAGE.route) {
            HomePage(itemViewModel = itemViewModel, navController)
        }
        composable(Routes.ITEM.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toLong()
            itemId?.let {
                ItemPage(itemId = it, itemViewModel = itemViewModel)
            }
        }



        // ... altre composable per altre rotte ...
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var value by remember { mutableStateOf(TextFieldValue("")) }
    Column {
        TextField(
            value = value,
            onValueChange = { newText ->
                value = newText
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "" +
                            "search Icon"
                )
            },
            label = { Text(text = "Ricerca") },
            modifier = Modifier
                .size(400.dp, 60.dp)
                .padding(8.dp),
            singleLine = true,
            placeholder = { Text(text = "Cerca i prodotti!") }
        )
    }


}



