package com.example.personacreatorjetpackcompose

import android.widget.EditText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

//----------------------------------------------メイン画面
@Composable
fun MainScreen(navController: NavHostController) {
    val textState = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Persona")
            Button(onClick = {
                navController.navigate("personaEdit")
            }) {
                Text(text = "新しいペルソナ作成")
            }
        OutlinedTextField(
            value = textState.value,
            onValueChange = { newText ->
                textState.value = newText
            },
            label = { Text("Enter your name") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("You entered: ${textState.value}")
    }
}