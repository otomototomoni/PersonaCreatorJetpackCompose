package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

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
    //外側のデザイン
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 上部真ん中に固定されたbutton
        Button(
            onClick = { navController.navigate("personaaddition") },
            modifier = Modifier
                .padding(top = 30.dp, bottom = 15.dp)
        ) {
            Text("ペルソナ新規作成")
        }

        //scrollできる内容
        MainScrollScreen(navController = navController,viewModel)
    }
}

//------------------------------------------preview
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MainScreen(navController = navController,viewModel = MainViewModel())
}