package com.example.personacreatorjetpackcompose

import android.icu.text.CaseMap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personacreatorjetpackcompose.ui.theme.PersonaCreatorJetpackComposeTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            PersonaCreatorJetpackComposeTheme {
                    CustomMainActivity()
            }
        }
    }
}

//-----------------------------------画面切り替えに必要なやつ
@Composable
fun CustomMainActivity(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "title") {
        composable("title"){ TitleScreen(navController) }
        composable("signup"){ SignUpScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("personaEdit") { PersonaEditScreen(navController) }
    }
}