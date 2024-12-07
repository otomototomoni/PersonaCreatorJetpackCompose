package ui

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/*
　サインアップするための画面
　下記は＠Composableで画面の出力を行っている。
  パスワードとメールアドレスを入力して新規登録をする画面
  引数として、viewModel:ViewModelではなく、viewModel:FirebaseAuthViewModelを使用している。
  ※ViewModelクラスにはauthというプロパティが定義されていないため。

  メモ：
  登録メールアドレスにメールを送る
  許可をしてからサインインができるようにする。
  メールアドレスがすでに使用されている場合に専用のエラーメッセージを出す。
 */
@Composable
fun SignUpScreen(navController: NavHostController,viewModel: MainViewModel){

    val context = LocalContext.current//エラー、成功をToastで出力するために必要なcontext
    val email = remember{ mutableStateOf("") }//登録するメールアドレス
    val password = remember{ mutableStateOf("") }//そのアドレスに対するパスワード

    //画面--------------------------------------------------------
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

        //新規登録ボタン-----------------------------
        Button(onClick = {
            /*
            　新規登録機能
            　パスワードが弱すぎる、メールアドレスが無効、すでに同じメールアドレスが登録されている。
            　↑これらでエラーが起こる。
              Firebase Authentication を使用して新しいユーザーアカウントを作成する処理。
            */
            //メールアドレスかパスワードが入力されていない場合の処理
            if(email.value.isEmpty() || password.value.isEmpty()){
                Toast.makeText(context,"メールアドレスとパスワードを入力してください",Toast.LENGTH_SHORT).show()
                return@Button
            //メールアドレスとパスワードが入力されている場合
            }else {
                viewModel.auth
                    .createUserWithEmailAndPassword(
                        email.value,
                        password.value
                    )//新しいユーザーアカウントを作成。emailとpasswordを参照している
                    .addOnCompleteListener { task ->//アカウント作成処理が完了したときに実行されるリスナーをお登録する。taskオブジェクトには、処理が成功したかどうか、エラーが発生したかどうかなどの情報が含まれている。

                        //Firebase Authentication 登録成功
                        if (task.isSuccessful) {
                            val user = viewModel.auth.currentUser//auth.currentUserで登録されたユーザー情報を取得できる
                            //追加事項：ユーザー情報を保存する処理（データベース）

                            //forebaseのユーザー情報をハッシュマップで保存
                            val userData = hashMapOf(
                                "email" to email.value,
                                "password" to password.value
                            )
                            //データベースのコレクションの作成とデータの保存
                                viewModel.db.collection("users").document(user!!.uid)
                                .set(userData)
                                //firestoreへのデータ書き込みが成功した場合に実行される
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully written!")
                                    //追加事項：ユーザーに確認メールを送信する。
                                    navController.navigate("login")//ログイン画面に戻る
                                }
                                //firestoreへのデータ書き込みが失敗した場合に実行される
                                .addOnFailureListener { e ->//eをリスナーの引数として宣言すると、リスナー内でeを参照できる。
                                    Log.w(TAG, "Error writing document", e)
                                }

                        //Firebase Authentication 登録失敗
                        } else {
                            //本番では使わない
                            val exception = task.exception//task.exceptionでエラーの情報を取得できる
                            //ログ出力ライブラリかカスタム例外ハンドラを使用する↓
                            exception?.printStackTrace()//printStackTraceはデバッグ用で本番環境では使わない

                            //エラーメッセージを表示する
                            //第一引数のcontextは上で定義したもの
                            Toast.makeText(
                                context,
                                "サインアップに失敗しました",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "サインアップエラー", exception)//ログに記録

                        }//task.isSuccessful-if-end

                    }//addOnCompleteListener-end
            }//null if-end
        },//onClick-end
            //onClickのボタンの大きさなど。
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "新規登録")
        }//button text

        //ログイン画面へ戻るボタン--------------------
        Button(onClick = {
            navController.navigate("login")
        },
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
        ){
            Text(text = "ログイン画面に戻る")
        }
    }//column-end
}//fun-end

//preview----------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    val navController = rememberNavController()
    SignUpScreen(navController = navController,viewModel = MainViewModel())//MainActivityのメソッド（FirebaseAuthViewModel（））を使用している。
}