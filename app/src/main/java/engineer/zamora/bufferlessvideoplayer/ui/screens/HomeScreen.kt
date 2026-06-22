package engineer.zamora.bufferlessvideoplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onNavigateToPlayer: (String) -> Unit) {
    // This is Compose State. If urlText changes, the UI automatically recomposes!
    var urlText by remember { mutableStateOf("http://10.0.2.2:8000/master.m3u8") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Welcome to the Video App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = urlText,
            onValueChange = { newText -> urlText = newText }, // Update state as user types
            label = { Text("Enter Video URL") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            // When clicked, we trigger the callback function passed from the NavHost
            onClick = { onNavigateToPlayer(urlText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = urlText.isNotBlank() // Disable button if empty!
        ) {
            Text("Open Video Player")
        }
    }
}