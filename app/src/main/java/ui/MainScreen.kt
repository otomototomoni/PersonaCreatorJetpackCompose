package ui

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
    新規ペルソナ作成で、保存を押すとペルソナが増えていくようにする。
    それぞれのペルソナを押すとそのペルソナの編集画面に移動する。
    MainScreenでは固定された外枠のデザイン部分を記述している。
    scroll部分はMainScrollScreenで記述している。

    メモ：
    スクロール部分がスクロールできない。多分他のviewの記述が必要
    scroll○○○○()のようなメソッドで管理していた。
 */
//----------------------------------------------メイン画面
@Composable
fun MainScreen(navController: NavHostController,viewModel: MainViewModel) {
    //Toast用のコンテキスト
    val context  = LocalContext.current
    var singOutpanel by remember { mutableStateOf(false) }
    val signOutOffset by animateFloatAsState (if (singOutpanel) 0f else -2000f)
    //外側のデザイン
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //サインアウトボタンと新規作成ボタン-------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //サインアウト
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 30.dp)
                    .clickable {
                        singOutpanel = !singOutpanel
                    }//clickable_end
            ) {
                Canvas(
                    modifier = Modifier
                        .size(50.dp) // 丸のサイズ
                        .background(Color.LightGray) // 背景色 (任意)
                ) {
                    drawCircle(
                        color = Color.Red, // 丸の色
                        radius = 50f, // 半径 (size の半分)
                    )
                }
            }
            // 上部真ん中に固定されたbutton
            Button(
                onClick = { navController.navigate("personaaddition") },
                modifier = Modifier
                    .padding(start = 50.dp, top = 30.dp, bottom = 15.dp)
            ) {
                Text("ペルソナ新規作成")
            }
        }
        //scrollできる内容
        MainScrollScreen(navController = navController,viewModel)
    }

    /*
    サインアウトの画面を表示
     */
    if(singOutpanel){
        //全体を灰色に
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(0.5f))
                .clickable {
                    singOutpanel = false
                },
            contentAlignment = Alignment.Center
        ) {
            //buttonを入れているbox
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(y = signOutOffset.dp)
                    .align(Alignment.Center)
                    .clickable {  }
                    .border(
                        width = 1.dp,
                        color = Color.Black
                    )
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "サインアウトしますか？",
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                    //サインアウトボタン
                    Button(
                        modifier = Modifier
                            .padding(10.dp),
                        //サインアウト処理
                        onClick = {
                            viewModel.viewModelScope.launch {
                                try {
                                    //サインアウト
                                    viewModel.auth.signOut()
                                    //ログイン画面へ遷移
                                    navController.navigate("login")
                                    //Toastを表示
                                    Toast.makeText(context, "サインアウトしました",
                                        Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "サインアウトに失敗しました", Toast.LENGTH_SHORT).show()
                                    Log.w("MainScreen", "サインアウトに失敗しました", e)
                                }
                            }
                        }
                    ) {
                        Text("サインアウト")
                    }
                }//Column_end
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    //サインアウト画面を閉じる
                    Button(
                        modifier = Modifier
                            .padding(
                                bottom = 10.dp
                            ),
                        //キャンセルボタン
                        onClick = {
                            singOutpanel = !singOutpanel
                        }
                    ) {
                        Text("閉じる")
                    }
                }
            }//サインアウトボタンの入っているBox_end
        }//全体の背景を灰色にするBox_end
    }//サインアウトボタンを出すためのif文_end
}//function_end

//------------------------------------------preview
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MainScreen(navController = navController,viewModel = MainViewModel())
}