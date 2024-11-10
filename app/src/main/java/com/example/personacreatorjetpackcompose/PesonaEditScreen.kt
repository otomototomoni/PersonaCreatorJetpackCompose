package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

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

    var backGroundColor by remember { mutableStateOf(Color.Yellow) }//付箋がクリックされたときに変更する

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
            .background(color = backGroundColor)//背景色が変更される
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
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp
                                    )
                                )
                                //クリックされたときの処理
                                .clickable {
                                    backGroundColor = item.color
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
                ) {
                    // ベースとなるBoxの内容
                    Text(text = personaName)
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
            ) {

                Box(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    Text(text = "下半分")
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
}

