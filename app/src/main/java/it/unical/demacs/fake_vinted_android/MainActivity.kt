package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.RetrofitClient
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Item
import it.unical.demacs.fake_vinted_android.model.User
import it.unical.demacs.fake_vinted_android.ui.theme.Fake_Vinted_AndroidTheme
import it.unical.demacs.fake_vinted_android.viewmodels.AddressFormViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.ItemViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserFormViewModel
import it.unical.demacs.fake_vinted_android.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fake_Vinted_AndroidTheme {
                val isLogged = remember { mutableStateOf(false) }
                val navController = rememberNavController()
                val context = LocalContext.current
                val sessionManager = remember { SessionManager(context) }
                val apiService = RetrofitClient.create(sessionManager,context)
                val userViewModel = UserViewModel(context)
                val userFormViewModel = UserFormViewModel()
                val itemViewModel = ItemViewModel(context)
                val addressFormViewModel = AddressFormViewModel()

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
                        itemViewModel = itemViewModel,
                        addressFormViewModel = addressFormViewModel

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
fun NavigationView(itemViewModel: ItemViewModel, userViewModel: UserViewModel,addressFormViewModel: AddressFormViewModel,userFormVIewModel : UserFormViewModel, apiService: ApiService, sessionManager: SessionManager, navController: NavHostController, isLogged: MutableState<Boolean>) {
    LaunchedEffect(key1 = true) {
        itemViewModel.loadFavorites()
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
                apiService = apiService,
                addressFormViewModel = addressFormViewModel,
            )
        }



        composable(Routes.ADDITEM.route) {
            AddItem(navController,apiService,sessionManager,navController) // La composable per aggiungere un articolo
        }
        composable(Routes.PROFILE.route) {
            ProfilePage(userViewModel = userViewModel, navController = navController, apiService = apiService,sessionManager=sessionManager)
        }
        composable(Routes.FIRSTPAGE.route) {
            HomePage(itemViewModel = itemViewModel, navController)
        }
        composable(Routes.ITEM.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toLong()
            itemId?.let {
                ItemPage(itemId = it, itemViewModel = itemViewModel, navController = navController, sessionManager = sessionManager,apiService=apiService)
            }
        }

        composable(Routes.OFFER.route){
            OfferPage(apiService = apiService, sessionManager =sessionManager , navController =navController )
        }

        composable(Routes.PURCHASE.route) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toLong()
            itemId?.let {
                PurchasePage(itemId = it, itemViewModel = itemViewModel, userViewModel = userViewModel, apiService = apiService, sessionManager = sessionManager, navController = navController)
            }
        }




        composable(Routes.SEARCH.route){
            SearchPage(apiService = apiService, sessionManager = sessionManager, navController =navController )
        }

        composable(Routes.NOTIFICATION.route){
            NotificationPage(apiService = apiService, sessionManager = sessionManager, navController = navController)
        }

    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(apiService: ApiService, sessionManager: SessionManager, navController: NavHostController) {
    val token = sessionManager.getToken()
    val searchResult = remember { mutableListOf<Item>() }
    val coroutineScope = rememberCoroutineScope()
    val showResult = remember { mutableStateOf(false) }
    val showError = remember { mutableStateOf(false) }
    var value by rememberSaveable { mutableStateOf("") }




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
    ) {innerpadding ->
        Column{

            TextField(
                value = value,
                onValueChange = { newText ->
                    searchResult.clear()
                    showResult.value = false
                    showError.value = false
                    value = newText
                },

                trailingIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val res = apiService.getSearch("Bearer $token", value, token)
                            if (res.isSuccessful) {
                                for (item in res.body()!!) {
                                    Log.d("oggetti", item.toString())
                                    searchResult.add(item)
                                }
                                showResult.value = true
                                value = ""
                            } else {
                                Log.d("funzionamento", "non va")
                            }
                            Log.d("valore", "risultati : $searchResult")
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                label = { Text(text = "Ricerca") },
                modifier = Modifier
                    .size(400.dp, 80.dp)
                    .padding(8.dp),
                singleLine = true,
                placeholder = { Text(text = "Cerca i prodotti!") }
            )
            /*if (showResult.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = innerpadding.calculateBottomPadding()),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(searchResult) { item ->
                        ItemPreview(
                            item,
                            navController,
                            sessionManager,
                            apiService,
                            favorites
                        )

                    }

                }


            }*/

        }


    }


}




