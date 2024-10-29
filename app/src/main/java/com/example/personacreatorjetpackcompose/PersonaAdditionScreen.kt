package com.example.personacreatorjetpackcompose

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions

//-----------------------------------ペルソナ作成画面
/*
    ペルソナの名前やタイトルを決める画面。

 */
@Composable
fun PersonaAdditionScreen(navController: NavHostController,viewModel:MainViewModel){

    var personaName = remember{ mutableStateOf("") }
//    var personaAge = remember{ mutableStateOf("") }
//    var personaHeight = remember{ mutableStateOf("") }
//    var personaWeight = remember{ mutableStateOf("") }
//    var personaSex = remember{ mutableStateOf("") }

    val personaDocuments = viewModel.db.collection("${viewModel.auth.currentUser!!.uid}").document("Persona")
    val context = LocalContext.current//Taastでエラーの出力、成功の出力に必要なcontext

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        //入力された内容をfirestoreに保存するボタン
        Text("PersonaEditScreen")

        OutlinedTextField(
            value = personaName.value,
            onValueChange = {personaName.value = it},
            label = { Text("Name") },
        )

        //入力された名前をfirestoreに保存するボタン
        Button(onClick = {
            //名前が入力されていない場合の処理
            if(personaName.value.isEmpty()){
                Toast.makeText(context,"名前を入力してください", Toast.LENGTH_SHORT).show()
                return@Button
            }else{
                //ドキュメントが存在するかどうか
                personaDocuments.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {//ドキュメントが存在するかどうかを判定
                            //updateはフィールドが存在しなかった時には、暗黙的にsetが呼ばれている。
                            document.reference.update(
                            "name",FieldValue.arrayUnion(personaName.value)
                            )
                        }else{//ドキュメントが存在しない場合にset
                            document.reference.set(
                                mapOf("name" to listOf(personaName.value)),
                                SetOptions.merge()
                            )
                        }
                    }
                navController.navigate("main")
            }
        }) {
            Text(text = "保存")
        }

        //前の画面に戻るボタン
        Button(onClick = {
            navController.navigate("main")//MainScreenに戻る
        },modifier = Modifier
            .padding(10.dp)
        ) { Text(text = "戻る") }

    }
}

//--------------------------------------preview
@Preview(showBackground = true)
@Composable
fun PersonaAdditionScreenPreview() {
    val navController = rememberNavController()
    PersonaAdditionScreen(navController = navController,viewModel = MainViewModel())
}
