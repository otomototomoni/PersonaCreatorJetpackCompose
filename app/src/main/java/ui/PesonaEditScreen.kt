package ui

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

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
    //付箋がクリックされたときに変更する
    var variableColor by remember { mutableStateOf(Color.Yellow) }
    //データベースに入れるためのフィールドを取得
    var variableText by remember { mutableStateOf("Geographic") }
    //TextFieldがフォーカスされているかどうかを判断
    var hasFocus by remember { mutableStateOf(false) }
    //行動変数や地理変数などTextFieldに入力された値を保存
    var textFields by remember { mutableStateOf<List<String>>(emptyList()) }
    //画面のスクロールするためのもの
    val scrollStateAbove = rememberScrollState()//画面スクロールを有効にする上のbox
    val scrollStateUnder = rememberScrollState()//画面スクロールを有効にする下のbox
    //付箋の大きさ
    val stickNoteHeight = 80.dp
    val stickNoteWidth = 80.dp
    //エラー処理に使用するcontext
    val context = LocalContext.current

    val items = listOf(
        Item("Geographic", "地理変数", Color.Yellow),
        Item("Demographic", "人口動態変数", Color.Red),
        Item("Psychographic", "心理的変数", Color.Blue),
        Item("Behavioral", "行動変数", Color.Green)
    )
    //この変数をtrueにする処理をクリックの処理に記述することで使用できないことを通知するTextを表示
    var unavailableShow by remember { mutableStateOf(false) }
    //navigationメニューで使用するやつ
    var menuVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (menuVisible) 1f else 0f)

    //画面全体の背景色
    var backgroundColor by remember { mutableStateOf(lerp(Color.White,variableColor,0.3f))}
    var borderColor by remember { mutableStateOf(lerp(Color.Black,variableColor,0.8f))}
    //付箋がクリックされて変更されたときに背景と枠線の色も変える
    LaunchedEffect(variableColor) {
        backgroundColor = lerp(Color.White,variableColor,0.1f)
        borderColor = lerp(Color.Black,variableColor,0.8f)
    }
    //画面全体--------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)//背景色が変更される
    ) {
        //-----------------------------------------------------------------上半分
        //BoxWithConstraintsで利用可能な最大幅と最大高さを取得できる。(MaxWidth等)
        BoxWithConstraints {
            val boxMaxWidth = maxWidth
            val boxMaxHeight = maxHeight
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .height(boxMaxHeight / 2)//画面全体の1/2を変数を記述するところにする
                    .clip(
                        RoundedCornerShape(
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
            ) {
                //ーーーーーーーーーーーーーーーー上の付箋部分
                LazyRow() {
                    items(items){item ->
                        Box(
                            modifier = Modifier
                                //boxを曲線にする
                                .clip(
                                    RoundedCornerShape(
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
                        .verticalScroll(scrollStateAbove)//垂直スクロールを有効にしている
                        .border(
                            width = 2.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .clip(
                            RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        //clipの後に背景をつけないと色が枠の外にはみ出る
                        .background(Color.White)
                ) {
                    Column() {
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
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        bottom = 8.dp
                                    )
                                    .fillMaxWidth()
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
                    //枠線の四隅を曲線にしている。
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(
                            16.dp
                        )
                    )
                    //表示範囲を曲線の内側だけにしている。
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    )
                    //clipの後に背景をつけないと色が枠の外にはみ出る
                    .background(Color.White)
            ) {
                /*
                    ペルソナの目標やゴール、年齢や性別を詳しく記載するところ
                    PersonaIntegration関数をBoxの中で実行している。
                 */
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .verticalScroll(scrollStateUnder)//垂直スクロールを有効にしている
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
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
                    onClick = {
                        unavailableShow = true
                    },
                    modifier = Modifier
                        .offset(y = UROffset.dp)
                        .padding(
                            top = 10.dp
                        )
                ) {
                    Text("ユーザーリサーチ(coming soon)")
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
                    top = (0.3 * LocalConfiguration.current.screenHeightDp).dp
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
/*
    まだ使用できない機能を使用しようとしたときに使えないことを示す
    変数unavailableShowをtrueにする処理をクリックの処理として設けることによってこの処理を使用できる。
 */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(0f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = unavailableShow) {
                Text(
                    text = "この機能は現在使用できません。",
                    fontWeight = FontWeight.Bold,
                    )
            }
        }
    }
    LaunchedEffect(unavailableShow) {
        if(unavailableShow) {
            delay(2000)
            unavailableShow = false
        }
    }
}//function_End---------------------------------------------------------------------------

/*
    共有のための処理
    TextFieldに入力されたメールアドレスをfirebaseで探してそのユーザーに共有する。
 */
fun Share(
    viewModel: MainViewModel,
    shareEmail : String,
    personaName : String,
    context : Context
){
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
                    //sharingPersonasドキュメントを取得
                    var shareDocument = viewModel.db.collection("${document.id}").document("sharingPersonas")
                    //sharingPersonasでisSuccessfulがtrueならset,そうじゃないならupdate
                    if(shareDocument.get().isSuccessful) {
                        shareDocument.set(data)
                    }else{
                        shareDocument.update(data)
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
fun PersonaIntegration(
    navController: NavHostController,
    viewModel: MainViewModel,
    personaName: String,
    userID: String,
){
    //Taastでエラーの出力、成功の出力に必要なcontext
    val context = LocalContext.current
    //TextFieldで入力しているかどうかを判断
    var hasFocus by remember { mutableStateOf(false) }
    val document = viewModel.db.collection("${userID}").document("${personaName}")
    //この機能が使用できないことを表示
    var unavaliableShow by remember { mutableStateOf<Boolean>(false) }

    //インスタンスの作成
    val cosutom = CustomUI(userID,personaName,viewModel,context)

    //UI------------------------------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            //画像の表示--------------------------------
            Box(
                modifier = Modifier
                    .size((LocalConfiguration.current.screenWidthDp * 0.5).dp)
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        /*
                        画像を入れれるようにする
                        入れた画像をMainScrollScreen.ktに表示できるようにする。
                        firebaseStorageが課金が必要そうなため断念
                         */
                        unavaliableShow = true
                    }
            ) {
                Text(
                    text = "   NoImage\n(coming soon)",
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
                cosutom.FeaturesTextField(
//                    userDataAge,
                    "年齢",
                    "age",
                    hasFocus
                )
                //性別---------------------------------------------------
                cosutom.FeaturesTextField(
//                    userDataGender,
                    "性別",
                    "gender",
                    hasFocus
                )
            }//Column_End
        }//Row_End

        //ペルソナのゴールについて記載するところ
        cosutom.SentenceTextField(
//            userDataGoal,
            "Goal",
            hasFocus
        )
        //ペルソナの問題について記載するところ
        cosutom.SentenceTextField(
//            userDataProblem,
            "Problem",
            hasFocus
        )
    }//Column_End

    //クリックした機能が使用できないことを表示する画面
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = unavaliableShow) {
                Text(
                    text = "この機能は現在使用できません",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    //unavaliableShowがtrueのとき、2秒後にunavaliableShowをfalseにする
    LaunchedEffect(unavaliableShow) {
        if (unavaliableShow) {
            delay(2000)
            unavaliableShow = false
        }
    }
}//function_End

//-------------------------------------------------------------------------------
/*
    共通するデータが多くあったのでクラスで保存
    そこからTextFieldなどを表示できるようにする
    メソッド
    ・SentenceTextField(保存したいデータ、データの名前、TextFieldがフォーカスされているかどうか)...長い文章で記載するときに使用
    ・FeaturesTextField(保存したいデータ、データの名前、データベースに保存するフィールド名、TextFieldがフォーカスされているかどうか)...年齢性別など短いものに
 */
class CustomUI(
    val userID: String,
    val personaName: String,
    val viewModel: MainViewModel,
    val context: Context
) {
    val document = viewModel.db.collection("${userID}").document("${personaName}")
    /*
    保存する要素を格納するところ
    例：ペルソナの目的や問題などの文章で記載するところの処理
 */
    @Composable
    fun SentenceTextField(
        dataText: String,
        Focus: Boolean
    ) {
        //TextFieldに入力された値を格納
        var data by remember { mutableStateOf("") }
        //TextFieldがフォーカスされているかどうかを判断
        var hasFocus by remember { mutableStateOf(Focus) }
        //fireStoreの変更を監視して変更があった場合に変数に入れる
//        document.addSnapshotListener{ snapshot, e ->
//            if (e != null) {
//                Log.w(TAG, "Listen failed.", e)
//                return@addSnapshotListener
//            }
//            if (snapshot != null && snapshot.exists()) {
//                data = snapshot.getString(dataText) ?: ""
//            } else {
//                Log.d(TAG, "Current data: null")
//            }
//        }
        //firebaseのデータを入れる。Unitは最初に１回だけ起動される。再起動を行わない
        LaunchedEffect(Unit) {
            document.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        data = document.get(dataText).toString() ?: ""
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
        }
        //TextFieldで入力された値を表示
        OutlinedTextField(
            value = data,
            onValueChange = { newText ->
                data = newText ?: ""
            },
            label = { Text(dataText) },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .onFocusChanged { focusState ->
                    if (hasFocus && !focusState.hasFocus) {
                        document
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    document
                                        .update(dataText, data)
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
                                        .set(mapOf(dataText to data))
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
        )
    }//SentenceTextField_function_end

    /*
    年齢や性別など簡易的な特徴をまとめておくためのTextFieldを入れるために使用する。
     */
    @Composable
    fun FeaturesTextField(
        dataText: String,
        dbFieldText: String,
        Focus: Boolean,//TextFieldがフォーカスされているかどうか
    ){
        var hasFocus by remember { mutableStateOf(Focus) }
        var userDataValue by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            document.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userDataValue = document.get(dbFieldText).toString() ?: ""
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
        }

        TextField(
            value = userDataValue,
            onValueChange = {newText ->
                userDataValue = newText ?: ""
            },
            label = { Text(dataText) },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (hasFocus && !focusState.hasFocus) {
                        document
                            .get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    document
                                        .update(dbFieldText, userDataValue)
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
                                        .set(mapOf(dbFieldText to userDataValue))
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
        )//TextField_end
    }//FeaturesTextField_function_end
}//class_end
