package it.unical.demacs.fake_vinted_android

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.unical.demacs.fake_vinted_android.ApiConfig.ApiService
import it.unical.demacs.fake_vinted_android.ApiConfig.SessionManager
import it.unical.demacs.fake_vinted_android.model.Notifications
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPage(apiService: ApiService, sessionManager: SessionManager) {
    var notificationResult = remember { mutableListOf<Notifications>() }
    val token = sessionManager.getToken()
    val coroutineScope = rememberCoroutineScope()

    // Launching the coroutine to fetch notifications
    coroutineScope.launch {
        val res = apiService.getUserNotification("Bearer $token", token)
        for (notification in res.body()!!) {
            notificationResult.add(notification)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( text = "Notifiche",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 130.dp))
                }
            )
        }
    ) {

        if (notificationResult.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(notificationResult) { notification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 70.dp, 5.dp, 0.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            notification.messaggio?.let { it1 ->
                                Text(
                                    text = it1,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        } else {
            // No notifications, display a message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Nessuna notifica da visionare!")
            }
        }
    }
}


    




