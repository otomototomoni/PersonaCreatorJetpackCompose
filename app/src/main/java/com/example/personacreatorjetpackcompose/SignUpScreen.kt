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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//画面
@Composable
fun SignUpScreen(navController: NavHostController,auth: FirebaseAuth){
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
        )//outlinedTextField

        //password input TextField
        OutlinedTextField(
            value = password.value,
            onValueChange = {password.value = it},
            label = { Text("password") },
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
                .height(56.dp)
        )//outlinedTextField

        //新規登録ボタン
        Button(onClick = {
            //ログイン機能
            //パスワードが弱すぎる、メールアドレスが無効、すでに同じメールアドレスが登録されている。
            //↑これらでエラーが起こる。
            auth
                .createUserWithEmailAndPassword(email.value, password.value)//新規登録処理を実行
                .addOnCompleteListener { task ->//処理の完了を監視し、成功または失敗に応じて処理を行う

                    if (task.isSuccessful) {//登録成功
                        val user = auth.currentUser//auth.currentUserで登録されたユーザー情報を取得できる
                        //追加事項：ユーザー情報を保存する処理（データベース）
                        //追加事項：ユーザーに確認メールを送信する。
                        navController.navigate("login")//ログイン画面に戻る

                    } else {//登録失敗
                        val exception = task.exception//task.exceptionでエラーの情報を取得できる
                        //ログ出力ライブラリかカスタム例外ハンドラを使用する↓
                        exception?.printStackTrace()//printStackTraceはデバッグ用で本番環境では使わない方がいい
                    }
                }
        },
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "新規登録")
        }//button text
    }//column
}//fun

//preview
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    val navController = rememberNavController()
    SignUpScreen(navController = navController,auth = Firebase.auth)
}