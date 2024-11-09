package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.constraintlayout.compose.Dimension
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import com.google.firebase.database.core.Tag

/*
    MainScreenのscroll部分
    ここに追加されたペルソナを標示していく
 */
data class ButtonData(val text: String, val documentId: String)//Gemini

@Composable
fun MainScrollScreen(navController: NavHostController,viewModel:MainViewModel,overpersonaName:String){

    val scrollState = rememberScrollState()//スクロール状態の管理をするためのもの
    //データベースの情報を取得(仮)
    val userpersonas = viewModel.db.collection("${viewModel.auth.currentUser!!.uid}").document("Persona")//collectionとdocumentを指定
    var dbpersonas by remember { mutableStateOf<List<String>>(emptyList())}//データベースに入っているペルソナを取得する用
    var personaname by remember { mutableStateOf("")}

    personaname = "satou"

    Box(
        modifier = Modifier
            .fillMaxSize()
            //boxの範囲を決めている
            .padding(
                start = (0.1f * LocalConfiguration.current.screenWidthDp).dp, // 左側10%のパディング
                top = (0.1f * LocalConfiguration.current.screenHeightDp).dp, // 上側20%のパディング
                end = (0.1f * LocalConfiguration.current.screenWidthDp).dp, // 右側10%のパディング
                bottom = (0.1f * LocalConfiguration.current.screenHeightDp).dp // 下側20%のパディング
            )
            //boxの外枠に線を標示
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )

            .verticalScroll(scrollState)//垂直スクロールを有効にしている

    ) {
        Column{
            LaunchedEffect(Unit) {
                //テスト：userに入っているメールアドレスを取得し、標示する。
                userpersonas.get()
                    .addOnSuccessListener { document ->
                        //dbpersonasにnameフィールドに入っている値を入れる
                        dbpersonas = document.get("name") as? List<String> ?: emptyList()
                    }
                }
            //ペルソナの数だけボタンを表示する。
            dbpersonas.forEach { persona ->
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    onClick = {
                        navController.navigate("personaedit/${persona}")//引数の渡し方がおかしい
                    }
                ){
                    Text(text = persona)
                }
            }
        }//column-end
    }//boc-end
}//function-end
