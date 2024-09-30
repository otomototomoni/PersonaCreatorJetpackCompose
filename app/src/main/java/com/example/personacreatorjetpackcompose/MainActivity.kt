package com.example.personacreatorjetpackcompose

import android.icu.text.CaseMap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.personacreatorjetpackcompose.ui.theme.PersonaCreatorJetpackComposeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/*ViewModelの初期化
　Firebase.authというログインやサインイン、ログアウトで使用する変数を定義。
　再生性などをしなくなるため、最近ではこのやり方がメイン
 */
class FirebaseAuthViewModel: ViewModel(){
    //Authentication
    lateinit var auth: FirebaseAuth//Lateinitで「後で初期化をする」という宣言ができる

    init{
        auth = Firebase.auth//Lateinitを使用したら必ず初期化しないといけない
    }
}

/*
　最初のonCreate関数
　Firebaseの初期化などを行っている。
　Navhostで画面情報をもったCustomMainActivityにログイン機能などに必要なFirebase.authを持っていく。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)//必要かどうかを検討

        FirebaseApp.initializeApp(this)//firebase初期化

        val viewModel = viewModel< FirebaseAuthViewModel >()//firebaseのログイン機能に必要な情報をviewModelで取得
        enableEdgeToEdge()
        setContent {
            PersonaCreatorJetpackComposeTheme {
                    CustomMainActivity(viewModel)
            }
        }
    }
}

//-----------------------------------画面切り替えに必要なやつ
@Composable
fun CustomMainActivity(auth:FirebaseAuth){//authはFirebaseの認証機能
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "title") {
        composable("title"){ TitleScreen(navController) }
        composable("login"){ LoginScreen(navController) }
        composable("signup"){ SignUpScreen(navController,auth) }
        composable("main") { MainScreen(navController) }
        composable("personaEdit") { PersonaEditScreen(navController) }
    }
}