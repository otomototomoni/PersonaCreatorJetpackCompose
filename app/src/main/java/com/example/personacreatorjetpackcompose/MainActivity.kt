package com.example.personacreatorjetpackcompose

import android.content.Context
import android.icu.text.CaseMap

//navArgumentのimport
import androidx.navigation.NavType
import androidx.navigation.navArgument

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//---------------------------------------------------------------
/*ViewModelの初期化
　Firebase.authというログインやサインイン、ログアウトで使用する変数を定義。
　再生性などをしなくなるため、最近ではこのやり方がメイン
 */
class MainViewModel: ViewModel(){
    //Authentication
    lateinit var auth: FirebaseAuth//Lateinitで「後で初期化をする」という宣言ができる
    lateinit var db: FirebaseFirestore//firestoreのインスタンスを生成
    init{
        auth = Firebase.auth//Lateinitを使用したら必ず初期化しないといけない
        db = Firebase.firestore
    }
}

//--------------------------------------------------------------------------
/*
　最初のonCreate関数
　Firebaseの初期化などを行っている。
　Navhostで画面情報をもったCustomMainActivityにログイン機能などに必要なFirebase.authを持っていく。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//必要かどうかを検討

        FirebaseApp.initializeApp(this)//firebase初期化

        enableEdgeToEdge()
        setContent {
            PersonaCreatorJetpackComposeTheme {
                    CustomMainActivity()
            }
        }
    }
}

//------------------------------------------------------画面切り替えに必要なやつ
@Composable
fun CustomMainActivity(){//authはFirebaseの認証機能

    val navController = rememberNavController()//画面遷移の管理
    val viewModel = viewModel< MainViewModel >()//firebaseのログイン機能に必要な情報をviewModelで取得

    //画面遷移一覧
    NavHost(navController = navController, startDestination = "title") {
        composable("title"){ TitleScreen(navController) }
        composable("login"){ LoginScreen(navController,viewModel) }
        composable("signup"){ SignUpScreen(navController,viewModel) }
        composable("main") { MainScreen(navController,viewModel) }
        composable("personaaddition") { PersonaAdditionScreen(navController,viewModel) }

        //ペルソナを編集する画面に移動するのに、ペルソナの名前で判断している。
        composable("personaedit/{personaName}",
        arguments = listOf(navArgument("personaName") {
            type = NavType.StringType
        })){backStackEntry ->
            val personaName = backStackEntry.arguments?.getString("personaName").toString()
            PersonaEditScreen(navController,viewModel,personaName)
        }
    }
}