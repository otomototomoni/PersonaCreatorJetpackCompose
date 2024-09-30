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
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/*
　サインアップするための画面
　下記は＠Composableで画面の出力を行っている。
  パスワードとメールアドレスを入力して新規登録をする画面
  引数として、viewModel:ViewModelではなく、viewModel:FirebaseAuthViewModelを使用している。
  ※ViewModelクラスにはauthというプロパティが定義されていないため。

  メモ：
  後は、データベースにメールアドレスとパスワードを保存してログインができるようにする。
 */
@Composable
fun SignUpScreen(navController: NavHostController,viewModel:FirebaseAuthViewModel){

    val email = remember{ mutableStateOf("") }//登録するメールアドレス
    val password = remember{ mutableStateOf("") }//そのアドレスに対するパスワード

    Column(
        //画面の大きさや位置を指定する
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("新規ユーザ登録")//一番上に表示される文字。

        //emailを入力するところ
        OutlinedTextField(
            value = email.value,
            onValueChange = {email.value = it},
            label = { Text("e-mail") },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp)
        )//outlinedTextField

        //passwordを入力するところ
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
            /*
            　ログイン機能
            　パスワードが弱すぎる、メールアドレスが無効、すでに同じメールアドレスが登録されている。
            　↑これらでエラーが起こる。
            */
            viewModel.auth
                .createUserWithEmailAndPassword(email.value, password.value)//新規登録処理を実行
                .addOnCompleteListener { task ->//処理の完了を監視し、成功または失敗に応じて処理を行う

                    //登録成功
                    if (task.isSuccessful) {
                        val user = viewModel.auth.currentUser//auth.currentUserで登録されたユーザー情報を取得できる
                        //追加事項：ユーザー情報を保存する処理（データベース）
                        //追加事項：ユーザーに確認メールを送信する。
                        navController.navigate("login")//ログイン画面に戻る
                    //登録失敗
                    } else {
                        val exception = task.exception//task.exceptionでエラーの情報を取得できる
                        //ログ出力ライブラリかカスタム例外ハンドラを使用する↓
                        exception?.printStackTrace()//printStackTraceはデバッグ用で本番環境では使わない方がいい
                    }

                }
        },//onClick
            //onClickのボタンの大きさなど。
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "新規登録")
        }//button text
    }//column
}//fun

//preview----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    val navController = rememberNavController()
    SignUpScreen(navController = navController,viewModel = FirebaseAuthViewModel())//MainActivityのメソッド（FirebaseAuthViewModel（））を使用している。
}