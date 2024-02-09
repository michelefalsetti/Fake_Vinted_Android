package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageItem(message = message)
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = message.sender, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message.content)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message.timestamp, style = MaterialTheme.typography.labelSmall)
        }
    }
}

data class Message(
    val sender: String,
    val content: String,
    val timestamp: String
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen() {
    val messages = remember {
        listOf(
            Message("Alice", "Ciao, come stai?", "10:00 AM"),
            Message("Bob", "Ciao, sto bene!", "10:05 AM"),
            Message("Alice", "Hai fatto qualcosa di interessante oggi?", "10:10 AM"),
            Message("Bob", "Ho lavorato sul progetto tutto il giorno.", "10:15 AM"),
            Message("Alice", "Va bene!", "10:20 AM")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Casella dei messaggi") }
            )
        }
    ) {
        MessageList(messages = messages)
    }
}

@Preview
@Composable
fun PreviewMessageScreen() {
    MessageScreen()
}
