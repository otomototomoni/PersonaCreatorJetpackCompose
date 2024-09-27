package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

//画面
@Composable
fun SignUpScreen(navController: NavHostController){

    val email = remember{ mutableStateOf("") }
    val password = remember{ mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("新規ユーザ登録")
        //email input TextField
        OutlinedTextField(
            value = email.value,
            onValueChange = {email.value = it},
            label = { Text("e-mail") },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        )
        //password input TextField
        OutlinedTextField(
            value = password.value,
            onValueChange = {password.value = it},
            label = { Text("password") },
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
                .height(56.dp)
        )
        //新規登録ボタン
        Button(onClick = {},
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "新規登録")
        }

    }
}

//preview
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    val navController = rememberNavController()
    SignUpScreen(navController = navController)
}