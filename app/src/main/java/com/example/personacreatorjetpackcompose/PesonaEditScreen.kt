package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.lang.reflect.Modifier

@Composable
fun PersonaEditScreen(navController: NavHostController,viewModel:MainViewModel){
    Text(text = "PersonaEditScreen")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "")

        Button(
            onClick = { navController.navigate("main") }
        ){
            Text("戻る")
        }
    }

}