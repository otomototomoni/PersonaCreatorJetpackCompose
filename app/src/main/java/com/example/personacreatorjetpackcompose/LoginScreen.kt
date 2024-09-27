package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navController: NavHostController){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ログイン画面")

        //email input TextField
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("e-mail") },
        )

        //password input TextField
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("password") },
        )

        //ログインボタン
        Button(onClick = {
            navController.navigate("main")
        }) {
            Text(text = "ログイン")
        }

        //新規登録ボタン
        Button(onClick = {
            navController.navigate("signup")
        }) {
            Text(text = "新規登録")
        }//button text
    }//column
}//fun

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}