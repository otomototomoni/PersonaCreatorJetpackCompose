package com.example.personacreatorjetpackcompose

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/*-------------------------------------------------------------ログイン画面
    ログイン画面
    メールアドレスとパスワードを打ってログインする。
    データベースに登録されているメールアドレスとパスワードと照合する。

    メモ：
    サインインしている場合にログイン画面を飛ばす機能
    セキュリティの強化。実際に何をすればいいかは謎
 */

@Composable
fun LoginScreen(navController: NavHostController,viewModel:FirebaseAuthViewModel){

    val email = remember{mutableStateOf("")}//入力されたメールアドレス
    val password = remember{mutableStateOf("")}//入力されたパスワード
    val context = LocalContext.current//Taastでエラーの出力、成功の出力に必要なcontext

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("ログイン画面")//一番上に標示される文字

        //ログインする用のメールアドレスを入力するところ
        OutlinedTextField(
            value = email.value,
            onValueChange = {email.value = it},
            label = { Text("e-mail") },
        )

        //ログインするようのパスワードを入力するところ
        OutlinedTextField(
            value = password.value,
            onValueChange = {password.value = it},
            label = { Text("password") },
        )

        /*
        ログインボタン
        ログインに成功するとメインスクリーンに画面遷移、Toastでログイン成功のメッセージ出す
         */
        Button(onClick = {
            //パスワードかメールアドレスがnullの場合の処理
            if(email.value.isEmpty() || password.value.isEmpty()){
                Toast.makeText( context,"メールアドレスとパスワードを入力してください",Toast.LENGTH_SHORT).show()
                return@Button
            //パスワードとメールアドレスがどちらとも入力されていた場合
            }else{
                viewModel.auth
                    .signInWithEmailAndPassword(email.value,password.value)//firebase Authenticationで提供されている。メールアドレスとパスワードを使用して、データベースと合致しているかどうかを判定
                    .addOnCompleteListener { task ->
                        //ログイン成功
                        if(task.isSuccessful){
                            navController.navigate("main")//メイン画面へ遷移
                            Toast.makeText( context,"ログインに成功しました",Toast.LENGTH_SHORT).show()//toastで”ログインに成功しました。”と表示
                        //ログイン失敗
                        }else{
                            Toast.makeText( context,"メールアドレス、パスワードが間違っています。",Toast.LENGTH_SHORT).show()//toastで”メールアドレス、パスワードが間違っています。”と表示
                        }//if-end
                    }//viewModel.auth-end
            }
        },//onclicklistener-end
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "ログイン")
        }//button-end

        //新規登録ボタン-----------------
        Button(onClick = {
            navController.navigate("signup")
        }) {
            Text(text = "新規登録")
        }//button text
    }//column
}//fun

//------------------------------------------preview
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    val navController = rememberNavController()
    LoginScreen(navController = navController,viewModel = FirebaseAuthViewModel())
}