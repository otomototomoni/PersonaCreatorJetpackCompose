package com.example.personacreatorjetpackcompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.security.AlgorithmParameters
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
fun MainScreen(navController: NavHostController) {

    //外側のデザイン
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        //scrollできる内容
        MainScrollScreen(navController = navController)

        // 右下に固定されたFAB
        FloatingActionButton(
            onClick = { /* FABクリック処理 */ },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 右下に固定
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "追加")
        }

        // 左上に固定されたハンバーガーメニュー
        IconButton(
            onClick = { /* ハンバーガーメニュークリック処理 */ },
            modifier = Modifier
                .align(Alignment.TopStart) // 左上に固定
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Menu, contentDescription = "ハンバーガーメニュー")
        }
    }


}

//------------------------------------------preview
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MainScreen(navController = navController)
}