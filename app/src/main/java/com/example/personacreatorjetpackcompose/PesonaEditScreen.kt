package com.example.personacreatorjetpackcompose

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.job
import kotlin.collections.toMutableMap

/*
    それぞれのペルソナの編集画面
    上半分で地理変数、人口動態変数、心理的変数、行動変数をナビゲーションメニューでそれぞれ表示させる。
    下半分でそれをまとめたペルソナを編集できるようにする
        -----→写真や年齢などをまとめる。
 */

//personaVariableを使用してfirebaseのフィールドなりなんなりを引っ張り出す。
data class Item(val personaVariable : String, val text : String, val color : Color)

@Composable
fun PersonaEditScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    personaName: String
) {

    var variableColor by remember { mutableStateOf(Color.Yellow) }//付箋がクリックされたときに変更する
    var variableText by remember { mutableStateOf("") }//変数名が入る

    var textFields by remember { mutableStateOf<List<String>>(emptyList()) }

    val scrollStateAbove = rememberScrollState()//画面スクロールを有効にする上のbox
    val scrollStateUnder = rememberScrollState()//画面スクロールを有効にする下のbox

    val stickNoteHeight = 80.dp//付箋の大きさ
    val stickNoteWidth = 80.dp//付箋の大きさ

    val items = listOf(
        Item("Geographic", "地理変数", Color.Yellow),
        Item("Demographic", "人口動態変数", Color.Red),
        Item("Psychographic", "心理的変数", Color.Blue),
        Item("Behavioral", "行動変数", Color.Green)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)//背景色が変更される
    ) {
        //-----------------------------------------------------------------上半分
        BoxWithConstraints {
            val boxMaxWidth = maxWidth
            val boxMaxHeight = maxHeight

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .height(boxMaxHeight / 2)//画面全体の1/2を変数を木尾述するところにする
            ) {

                //ーーーーーーーーーーーーーーーー上の付箋部分
                LazyRow() {
                    items(items){item ->

                        Box(
                            modifier = Modifier
                                //boxを曲線にする
                                .clip(
                                    RoundedCornerShape
                                        (
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                    )
                                )
                                .background(item.color)
                                .size(width = stickNoteWidth, height = stickNoteHeight)
                                //クリックされたときの処理
                                .clickable {
                                    variableColor = item.color
                                    variableText = item.personaVariable
                                }
                        ) {
                            Text(text = item.text)
                        }

                    }

                }//LazyRow_End

                //地理的変数などの変数を入れるところーーーー
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .verticalScroll(scrollStateAbove)//垂直スクロールを有効にしている
                        .border(
                            width = 1.dp,
                            color = variableColor,
                            shape = RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                ) {
                    Column {
                        for ((index, text) in textFields.withIndex()) {
                            var currentText by remember(key1 = index) { mutableStateOf(text) } // 各TextFieldの状態を管理

                            OutlinedTextField(
                                value = currentText,
                                onValueChange = { currentText = it },
                                label = { Text("TextField ${index + 1}") }, // ラベルに番号を付ける
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

            }//Column_End

        }//BoxWithConstraints_End

        //-----------------------------------------------------------------下半分
        BoxWithConstraints {
            val boxMaxWidth = maxWidth
            val boxMaxHeight = maxHeight
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .height(boxMaxHeight / 4 * 3)
                    .width(boxMaxWidth)
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(
                            16.dp
                        )
                    )
            ) {

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .verticalScroll(scrollStateUnder)//垂直スクロールを有効にしている
                ) {
                    PersonaIntegration(navController,viewModel,personaName)//　　↓
                }
            }//Column_End
        }//BoxWithConstraints_End

        Box() {
            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .align(Alignment.BottomCenter)//alignはboxの中でだけ生息している。
            ) {
                Text("戻る")
            }
        }

    }//全体のcolumnの最後の}---------------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Button(
            onClick = { textFields = textFields + "" },
            modifier = Modifier
                .offset { IntOffset(700, 1100) }//自分の好きな位置にボタンを表示
        ) {
            Text("追加")
        }
    }
}

//------------------------------------------------------------------------------------------
/*
    ペルソナについてまとめるところ
    ・ペルソナの画像、名前、年齢、性別
    ・ペルソナの課題、ペルソナのゴール、悩みなどをまとめる
 */

//data class UserData(
//    var age : String = "",
//    var gender : String = "",
//    var personaGoal : String = "",
//    var personaProblem : String = ""
//)

@Composable
fun PersonaIntegration(navController: NavHostController,viewModel: MainViewModel,personaName: String){

    //Rowの大きさを取得している
    //var rowWidth by remember { mutableStateOf(0) }
    //Rowの大きさを取得し、反映させるのが遅いため、derivedStateOfで動きを監視してから変更させている。
    //val halfRowWidth by remember(rowWidth) { derivedStateOf { rowWidth / 2 } }

    //年齢とか色々
//    var userData by remember {
//        mutableStateOf(
//            UserData(
//                age = "",
//                gender = "",
//                personaGoal = "",
//                personaProblem = ""
//            )
//        )
//    }
    var userDataAge by remember { mutableStateOf("") }
    var userDataGender by remember { mutableStateOf("") }
    var userDataGoal by remember { mutableStateOf("") }
    var userDataProblem by remember { mutableStateOf("") }

    val document = viewModel.db.collection("${viewModel.auth.currentUser!!.uid}").document("${personaName}")

    //firebaseのデータを入れる。
    LaunchedEffect (Unit) {
        document.get().addOnSuccessListener { document ->
            if (document.exists()) {
                userDataAge = document.get("age").toString() ?: ""
                userDataGender = document.get("gender").toString()?: ""
                userDataGoal = document.get("Goal").toString()?: ""
                userDataProblem = document.get("Problem").toString()?: ""
            } else {
                Log.d(TAG, "No such document")
            }
        }
    }

    //Taastでエラーの出力、成功の出力に必要なcontext
    val context = LocalContext.current
    var hasFocus by remember { mutableStateOf(false) }

    //UI------------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Row(
            modifier = Modifier
                .padding(10.dp)
            //Rowのwidthの大きさを取得
//            .onGloballyPositioned { coordinates ->
//                rowWidth = coordinates.size.width
//            }
        ) {
            //画像の表示--------------------------------
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "NoImage",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }//Box_End

            //名前と年齢の表示--------------------------
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
            ) {
                //ペルソナの名前
                Text(
                    text = personaName,
                    fontSize = 30.sp
                )
                //年齢-------------------------------------------------------
                TextField(
                    value = userDataAge,
                    onValueChange = {newText ->
                        userDataAge = newText ?: ""
                        document.get()
                            .addOnSuccessListener { snapshot ->
                                if(snapshot.exists()){
                                    document.update("age",newText).addOnSuccessListener {
                                        Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(context,"更新失敗",Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    document.set(mapOf("age" to newText))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        }.addOnFailureListener {
                                             e -> Log.w(TAG, "Error updating document", e)
                                        }
                                }//if_End
                            }//get Success
                            .addOnFailureListener {
                                 e -> Log.w(TAG, "Error updating document", e)
                            }
                    },
                    label = { Text("年齢") },
                    modifier = Modifier
                        .padding(
                            10.dp
                        )
                        .fillMaxWidth()
                )
                //性別---------------------------------------------------
                TextField(
                    value = userDataGender,
                    onValueChange = {newText ->
                        userDataGender = newText ?: ""
                    },
                    label = { Text("性別") },
                    modifier = Modifier
                        .onFocusChanged { focusState ->
                            if(hasFocus && !focusState.hasFocus){
                                document.get()
                                    .addOnSuccessListener { snapshot ->
                                        if(snapshot.exists()){
                                            document.update("gender",userDataGender).addOnSuccessListener {
                                                Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener {
                                                Toast.makeText(context,"更新失敗",Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            document.set(mapOf("gender" to userDataGender))
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                                                }.addOnFailureListener {
                                                        e -> Log.w(TAG, "Error updating document", e)
                                                }
                                        }//if_End
                                    }//get Success
                                    .addOnFailureListener {
                                            e -> Log.w(TAG, "Error updating document", e)
                                    }
                            }
                            hasFocus = focusState.hasFocus
                        }
                        .padding(
                            10.dp
                        )
                        .fillMaxWidth()
                )//TextField_End------------------------------------------
            }//Column_End
        }//Row_End

        //personaの目標
        OutlinedTextField(
            value = userDataGoal,
            onValueChange = { newText ->
                userDataGoal = newText ?: ""
            },
            label = { Text("Goal") },
            modifier = Modifier
                .onFocusChanged { focusState ->
                    if(hasFocus && !focusState.hasFocus){
                        document.get()
                            .addOnSuccessListener { snapshot ->
                                if(snapshot.exists()){
                                    document.update("Goal",userDataGoal).addOnSuccessListener {
                                        Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(context,"更新失敗",Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    document.set(mapOf("Goal" to userDataGoal))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        }.addOnFailureListener {
                                                e -> Log.w(TAG, "Error updating document", e)
                                        }
                                }//if_End
                            }//get Success
                            .addOnFailureListener {
                                    e -> Log.w(TAG, "Error updating document", e)
                            }
                    }
                    hasFocus = focusState.hasFocus
                }
                .padding(
                    15.dp
                )
                .height(300.dp)
                .fillMaxWidth()
        )
        //personaの問題
        OutlinedTextField(
            value = userDataProblem,
            onValueChange = { newText ->
                userDataProblem = newText ?: ""
            },
            label = { Text("Problem") },
            modifier = Modifier
                .onFocusChanged { focusState ->
                    if(hasFocus && !focusState.hasFocus){
                        document.get()
                            .addOnSuccessListener { snapshot ->
                                if(snapshot.exists()){
                                    document.update("Problem",userDataProblem).addOnSuccessListener {
                                        Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(context,"更新失敗",Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    document.set(mapOf("Problem" to userDataProblem))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        }.addOnFailureListener {
                                                e -> Log.w(TAG, "Error updating document", e)
                                        }
                                }//if_End
                            }//get Success
                            .addOnFailureListener {
                                    e -> Log.w(TAG, "Error updating document", e)
                            }
                    }
                    hasFocus = focusState.hasFocus
                }
                .padding(
                    15.dp
                )
                .height(300.dp)
                .fillMaxWidth()
        )

    }//Column_End
}//function_End
