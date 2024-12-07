package ui

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay

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
    personaName: String,
    userID: String
) {
    val document = viewModel.db.collection("${userID}").document("${personaName}")

    var variableColor by remember { mutableStateOf(Color.Yellow) }//付箋がクリックされたときに変更する
    var variableText by remember { mutableStateOf("Geographic") }//データベースに入れるためのフィールドを取得

    var hasFocus by remember { mutableStateOf(false) }

    var textFields by remember { mutableStateOf<List<String>>(emptyList()) }

    val scrollStateAbove = rememberScrollState()//画面スクロールを有効にする上のbox
    val scrollStateUnder = rememberScrollState()//画面スクロールを有効にする下のbox

    val stickNoteHeight = 80.dp//付箋の大きさ
    val stickNoteWidth = 80.dp//付箋の大きさ

    val context = LocalContext.current

    val items = listOf(
        Item("Geographic", "地理変数", Color.Yellow),
        Item("Demographic", "人口動態変数", Color.Red),
        Item("Psychographic", "心理的変数", Color.Blue),
        Item("Behavioral", "行動変数", Color.Green)
    )

    //navigationメニューで使用するやつ
    var menuVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (menuVisible) 1f else 0f)
    //navigationメニューで使用するボタンのＸ軸の上に並べたいため、ボタンのｘ軸を取得する変数
//    var navigationButtonX by remember{ mutableStateOf(0f) }
//    var navigationButtonY by remember{ mutableStateOf(0f) }

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
                    .height(boxMaxHeight / 2)//画面全体の1/2を変数を記述するところにする
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
                        LaunchedEffect(variableText){
                            textFields = emptyList()
                            document.get().addOnSuccessListener { document ->
                                if(document.exists()){
                                    textFields = document.get(variableText) as? List<String> ?: emptyList()
                                }
                            }
                        }
                        //TextFieldから入力された値を受け取る
                        for ((index, text) in textFields.withIndex()) {
                            var currentText by remember(key1 = index) { mutableStateOf(text) } // 各TextFieldの状態を管理
                            OutlinedTextField(
                                value = currentText,
                                onValueChange = { currentText = it },
                                label = { Text("TextField ${index + 1}") }, // ラベルに番号を付ける
                                modifier = Modifier
                                    .onFocusChanged { focusState ->
                                        //このTextFieldから出ようとしたときにだけfirestoreを更新する処理
                                        if (hasFocus && !focusState.hasFocus) {
                                            document
                                                .get()
                                                .addOnSuccessListener { snapshot ->
                                                    //snapshot（firestoreのデータ）が見つかったとき
                                                    if (snapshot.exists()) {
                                                        val List =
                                                            snapshot.get(variableText) as? List<String>
                                                        val updateList =
                                                            List?.toMutableList() ?: mutableListOf()

                                                        //if文がないと空のlistにaccessしようとしていることになるから
                                                        if (index < updateList.size) {
                                                            updateList[index] = currentText
                                                        } else {
                                                            updateList.add(currentText)
                                                        }
                                                        //取得成功
                                                        document
                                                            .update(variableText, updateList)
                                                            .addOnSuccessListener {
                                                                Toast
                                                                    .makeText(
                                                                        context,
                                                                        "更新成功",
                                                                        Toast.LENGTH_SHORT
                                                                    )
                                                                    .show()
                                                            }
                                                            //取得失敗
                                                            .addOnFailureListener {
                                                                Toast
                                                                    .makeText(
                                                                        context,
                                                                        "更新失敗",
                                                                        Toast.LENGTH_SHORT
                                                                    )
                                                                    .show()
                                                            }
                                                        //snapshot（firestoreのデータ）が見つからなかったとき
                                                    } else {
                                                        val initialList =
                                                            MutableList(textFields.size) { "" }
                                                        initialList[index] = currentText
                                                        document
                                                            .set(mapOf(variableText to initialList))
                                                            .addOnSuccessListener {
                                                                Log.d(
                                                                    TAG,
                                                                    "DocumentSnapshot successfully updated!"
                                                                )
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Log.w(
                                                                    TAG,
                                                                    "Error updating document",
                                                                    e
                                                                )
                                                            }
                                                    }//if_End
                                                }//get Success
                                                .addOnFailureListener { e ->
                                                    Log.w(TAG, "Error updating document", e)
                                                }
                                        }
                                        hasFocus = focusState.hasFocus
                                    }
                                    .padding(bottom = 8.dp)
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
                    PersonaIntegration(navController,viewModel,personaName,userID)//　　↓
                }
            }//Column_End
        }//BoxWithConstraints_End
        //Navigationメニュー---------------------------------------------------------
        Button(
            onClick = { menuVisible = !menuVisible },
            modifier = Modifier
                .align(Alignment.End)
                /*
                このボタンのＸ軸を取得
                　onGloballyPositioned はコンポサブる関数がレイアウトされた後に呼び出される
                　LayoutCoordinatesクラスに属しており、そのクラスのpositionInRoot()メソッドでボタンのＸ軸を取得
                */
//                .onGloballyPositioned { coordinates ->
//                    navigationButtonX = coordinates.positionInRoot().x
//                    navigationButtonY = coordinates.positionInRoot().y
//                }
        ) {
            Text("Menu")
        }
    }//全体のcolumnの最後の}---------------------------------------------------------------

    /*
        それぞれの変数のTedtFieldを追加するボタン
        真ん中右端に配置
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Button(
            onClick = { textFields = textFields + "" },
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            Text("追加")
        }
    }

    //Navigationメニュー処理------------------------------------------------------
    var ShareBox by remember { mutableStateOf(false) }//ShareBoxを表示するための変数
    val ShareBoxOffset by animateFloatAsState(if (ShareBox) 0f else -2000f)
    if (menuVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.5f))
                .clickable { menuVisible = false }
        ) {
            Column(
                modifier = Modifier
                    .alpha(alpha)
                    .fillMaxSize(),
               horizontalAlignment = Alignment.End
            ) {

                // メニュー項目
                var MBVisible by remember { mutableStateOf(false) }
                val MBOffset by animateFloatAsState(if (MBVisible) 0f else 2000f)
                Button(
                    onClick = { navController.navigate("main")},
                    modifier = Modifier
                        .offset(y = MBOffset.dp)
                        .padding(
                            top = (0.7 * LocalConfiguration.current.screenHeightDp).dp
                        )
                ){
                    Text("メイン画面へ戻る")
                }

                var URVisible by remember { mutableStateOf(false) }
                val UROffset by animateFloatAsState(if (URVisible) 0f else 2000f)
                Button(
                    onClick = { },
                    modifier = Modifier
                        .offset(y = UROffset.dp)
                        .padding(
                            top = 10.dp
                        )
                ) {
                    Text("ユーザーリサーチ(commingsoom)")
                }

                var ShareVisible by remember { mutableStateOf(false) }
                val ShareOffset by animateFloatAsState(if (ShareVisible) 0f else 2000f)
                Button(
                    onClick = {
                        if(ShareBox){
                            ShareBox = false
                        }else{
                            ShareBox = true
                        }
                    },
                    modifier = Modifier
                        .offset(y = ShareOffset.dp)
                        .padding(
                            top = 10.dp
                        )
                ) {
                    Text("共有")
                }

                LaunchedEffect(menuVisible) {
                    if (menuVisible) {
                        delay(50)
                        ShareVisible = true
                        delay(50)
                        URVisible = true
                        delay(50)
                        MBVisible = true
                    } else {
                        MBVisible = false
                        URVisible = false
                        ShareVisible = false
                    }
                }
            }
        }
    } //Navigation_end-------------------------------------------------------------------------------------
// 共有情報を入力----------------------------------------------------------
    var shareEmail by remember{mutableStateOf("")}
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .offset(y = (ShareBoxOffset).dp)
                .padding(
                    top = (0.4 * LocalConfiguration.current.screenHeightDp).dp
                )
                .background(Color.White)
                .size(250.dp, 300.dp)
                .border(1.dp, Color.Black)
                .clickable { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "共有",
                )
                OutlinedTextField(
                    value = shareEmail,
                    onValueChange = { shareEmail = it },
                    label = { Text("共有する相手のe-mail") },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Button(onClick = {Share(viewModel,shareEmail,personaName,context)},
                    modifier = Modifier
                        .padding(top = 16.dp)
                ) {
                    Text(text = "共有")
                }
                Button(onClick = { ShareBox = false },
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(text = "閉じる")
                }
            }
        }
    }//ShareBoxColumn_end
}//function_End---------------------------------------------------------------------------

/*
    共有のための処理
    TextFieldに入力されたメールアドレスをfirebaseで探してそのユーザーに共有する。
 */
fun Share(viewModel: MainViewModel,shareEmail : String,personaName : String,context : Context){

    val usersCollection = viewModel.db.collection("users")
    var succes = false
    usersCollection.get()
        //コレクションがget()できたときの処理
        .addOnSuccessListener { documents ->
            //取得したドキュメントを一つずつ見て、メールアドレスが合致したものを確認
            documents.forEach { document ->
                if(document.get("email") == shareEmail){
                    //このように１つにまとめないと上書きが行われる。
                    val data = mapOf(
                        "shareUserIDs" to FieldValue.arrayUnion("${viewModel.auth.currentUser!!.uid},${personaName}")
                    )
                    //documentが存在した場合にはupdate,存在しなかった場合にはset
                    if(document.exists()) {
                        viewModel.db.collection("${document.id}").document("sharingPersonas").update(data)
                    }else{
                        viewModel.db.collection("${document.id}").document("sharingPersonas").set(data)
                    }
                    succes = true
                }
            }
            //メールアドレスが見つかったとき、見つからなかった時の処理
            if(succes){
                Toast.makeText(context,"共有に成功しました",Toast.LENGTH_SHORT).show()
                succes = false
            }else{
                Toast.makeText(context,"共有に失敗しました",Toast.LENGTH_SHORT).show()
            }
        }
        //コレクションがget()できなかった時の処理
        .addOnFailureListener{ e ->
            Log.w(TAG, "Error getting documents", e)
        }
}

//------------------------------------------------------------------------------------------
/*
    ペルソナについてまとめるところ
    ・ペルソナの画像、名前、年齢、性別
    ・ペルソナの課題、ペルソナのゴール、悩みなどをまとめる
 */

@Composable
fun PersonaIntegration(navController: NavHostController, viewModel: MainViewModel, personaName: String,userID: String){

    var userDataAge by remember { mutableStateOf("") }
    var userDataGender by remember { mutableStateOf("") }
    var userDataGoal by remember { mutableStateOf("") }
    var userDataProblem by remember { mutableStateOf("") }

    val document = viewModel.db.collection("${userID}").document("${personaName}")

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
                            if (hasFocus && !focusState.hasFocus) {
                                document
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if (snapshot.exists()) {
                                            document
                                                .update("gender", userDataGender)
                                                .addOnSuccessListener {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "更新成功",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                                .addOnFailureListener {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "更新失敗",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                        } else {
                                            document
                                                .set(mapOf("gender" to userDataGender))
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot successfully updated!"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(TAG, "Error updating document", e)
                                                }
                                        }//if_End
                                    }//get Success
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error updating document", e)
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
                    if (hasFocus && !focusState.hasFocus) {
                        document
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    document
                                        .update("Goal", userDataGoal)
                                        .addOnSuccessListener {
                                            Toast
                                                .makeText(context, "更新成功", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        .addOnFailureListener {
                                            Toast
                                                .makeText(context, "更新失敗", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                } else {
                                    document
                                        .set(mapOf("Goal" to userDataGoal))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error updating document", e)
                                        }
                                }//if_End
                            }//get Success
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
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
                    if (hasFocus && !focusState.hasFocus) {
                        document
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    document
                                        .update("Problem", userDataProblem)
                                        .addOnSuccessListener {
                                            Toast
                                                .makeText(context, "更新成功", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                        .addOnFailureListener {
                                            Toast
                                                .makeText(context, "更新失敗", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                } else {
                                    document
                                        .set(mapOf("Problem" to userDataProblem))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error updating document", e)
                                        }
                                }//if_End
                            }//get Success
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
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
