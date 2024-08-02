package com.example.personacreatorjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personacreatorjetpackcompose.ui.theme.PersonaCreatorJetpackComposeTheme
import com.example.personacreatorjetpackcompose.ui.theme.PersonaEditScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonaCreatorJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CustomMainActivity(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

//-----------------------------------Main画面のView
@Composable
fun CustomMainActivity(modifier:Modifier = Modifier){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("personaEdit") { PersonaEditScreen(navController) }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    Column {
        Text("New Persona")
        Button(onClick = {
            navController.navigate("personaEdit")
        }) {
            Text(text = "新しいペルソナ作成")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PersonaCreatorJetpackComposeTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable("main") { MainScreen(navController) }
            composable("personaEdit") { PersonaEditScreen(navController) }
        }
        MainScreen(navController)
    }
}