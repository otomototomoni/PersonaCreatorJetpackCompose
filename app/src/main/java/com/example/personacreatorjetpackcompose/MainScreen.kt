package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.security.AlgorithmParameters

//----------------------------------------------メイン画面
@Composable
fun MainScreen(navController: NavHostController) {

    val num1 = remember{ mutableStateOf("") }
    val num2 = remember{ mutableStateOf("") }
    val sum = remember{ mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Persona")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            //ペルソナ作成ボタン
            Button(
                onClick = { navController.navigate("personaEdit") }
            ) {
                Text(
                    text = "新しいペルソナ作成"
                )
            }

            //数字の入力
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                OutlinedTextField(
                value = num1.value,
                onValueChange = {num1.value = it},
                label = { Text("1つ目の数字") },
                    modifier = Modifier
                        .width(100.dp)
                        .weight(1f)
                )
                //間隔
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
                OutlinedTextField(
                    value = num2.value,
                    onValueChange = {num2.value = it},
                    label = { Text("2つ目の数字") },
                    modifier = Modifier
                        .width(100.dp)
                        .weight(1f)
                )

            }//Row

            Button(
                onClick = { sum.value = (num1.value.toIntOrNull()?:0) + (num2.value.toIntOrNull()?:0) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "足し算",
                )
            }
            Text(
                "計算結果 --> ${sum.value} ",
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MainScreen(navController = navController)
}