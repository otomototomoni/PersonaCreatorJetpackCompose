package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlin.math.log

/*
    MainScreenのscroll部分
    ここに追加されたペルソナを標示していく
 */
data class ButtonData(val text: String, val documentId: String)//Gemini

@Composable
fun MainScrollScreen(navController: NavHostController,viewModel: MainViewModel){

    val scrollState = rememberScrollState()//スクロール状態の管理をするためのもの
    val scrollStateUnder = rememberScrollState()//スクロール状態の管理をするためのもの
    //データベースの情報を取得(仮)
    val userpersonas = viewModel.db.collection("${viewModel.auth.currentUser!!.uid}").document("Persona")//collectionとdocumentを指定
    val sharePersonas = viewModel.db.collection("${viewModel.auth.currentUser!!.uid}").document("sharingPersonas")//collectionとdocumentを指定
    var dbpersonas by remember { mutableStateOf<List<String>>(emptyList())}//データベースに入っているペルソナを取得する用
    var dbSharePersonas by remember { mutableStateOf<List<String>>(emptyList())}

    Box(
        modifier = Modifier
            .size(
                width = (0.8f * LocalConfiguration.current.screenWidthDp).dp,
                height = (0.5f * LocalConfiguration.current.screenHeightDp).dp
            )
            //boxの外枠に線を標示
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .verticalScroll(scrollState)//垂直スクロールを有効にしている
    ) {
        Column {
                //ログインしているユーザーが登録しているペルソナの名前を取得
                userpersonas.get()
                    .addOnSuccessListener { document ->
                        //dbpersonasにnameフィールドに入っている値を入れる
                        dbpersonas = document.get("name") as? List<String> ?: emptyList()
                    }
                //ペルソナの数だけボタンを表示する。
                dbpersonas.forEach { persona ->
                    PersonaBox(navController,persona,"${viewModel.auth.currentUser!!.uid}")
                }
        }//column-end
    }//box-end
    /*
    　　シェアされたペルソナを表示する
     */
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width((0.8f * LocalConfiguration.current.screenWidthDp).dp)
            .padding(
                top = 10.dp,
                bottom = 25.dp
            )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .verticalScroll(scrollStateUnder)
    ){
        Column {
            //シェアされているペルソナがいる場合にListに格納
            sharePersonas.get()
                .addOnSuccessListener { documents ->
                    dbSharePersonas = documents.get("shareUserIDs") as? List<String> ?: emptyList()
                }
                .addOnFailureListener { e ->
                    println("Error getting documents: $e")
                }
            println(dbSharePersonas)
            //シェアされているペルソナの表示
            var userIdPersona by remember { mutableStateOf<List<String>>(emptyList()) }
            dbSharePersonas.forEach { sharePersona ->
                userIdPersona = sharePersona.split(",")
                PersonaBox(navController, userIdPersona[1], userIdPersona[0])
            }
        }
    }//box-end
}//function-end

//----------------------------------------------------------------------------------

/*
    ペルソナの一覧を標示するときの一つ一つのデザイン
 */
@Composable
fun PersonaBox(navController: NavHostController,persona : String,userID : String){

    val color = Color.Black
    val lighterColor = color.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            //クリックしたときの処理
            .clickable {
                navController.navigate("personaedit/${persona}/${userID}")
            }
    ){
        //--------------------------------------------------------------------
        //画像の右に名前と備考欄をつける。
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            //イメージ写真の資格を描画。一番左に置く
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .size(width = 100.dp, height = 100.dp)
            ) {
                Text(
                    text = "NoImage",
                    color = lighterColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
            ){
                //ペルソナの名前を表示
                Text(
                    text = persona,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                //備考欄
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        )
                ){
                    Text(
                        text = "備考欄",
                        color = lighterColor,
                        modifier = Modifier
                            .padding(10.dp)
                    )
                }
            }
        }//Column_End--------------------------------------------------------
    }//一番外枠のBox_End
}//function_End
