package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unical.demacs.fake_vinted_android.ui.theme.Fake_Vinted_AndroidTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fake_Vinted_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    //AppNavigation()
                    MainPage()


                }
            }
        }
    }
}



@Composable
fun MainPage(){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxHeight()
    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.padding(vertical =90.dp))
        Text(
            fontFamily = FontFamily.Monospace,
            text = "Per continuare ad usare l'app è necessario eseguire l'accesso.",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical =10.dp))

        Button(onClick = { /*TODO*/ }, shape= RoundedCornerShape(10.dp)) {
            Text(text = "        Iscriviti        ")

        }
        Text(
            fontFamily = FontFamily.Monospace,
            text = "oppure",
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = Color.Black
        )


        Button(onClick = { }, shape= RoundedCornerShape(10.dp)) {
            Text(text = "        Accedi        ")

        }
    }

}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(){
    Scaffold(
        bottomBar = {
            BottomAppBar{
                Row(
                    modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            )
                    }
                IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    }
            }
            }

        }

    ){
    }
    SearchBar()
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


