package com.example.pianomemo.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pianomemo.ui.theme.PianoMemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "設定",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            val items = listOf(
                "このアプリについて" to {},
                "お問い合わせ" to {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@example.com")
                        putExtra(Intent.EXTRA_SUBJECT, "お問い合わせ")
                    }
                    context.startActivity(emailIntent)
                },
                "レビューする" to {
                    val reviewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.example.app"))
                    context.startActivity(reviewIntent)
                },
                "このアプリを共有する" to {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "このアプリをチェックしてみて！\nhttps://play.google.com/store/apps/details?id=com.example.app")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "アプリを共有"))
                },
                "バージョン 1.0.0" to {}
            )

            items.forEach { (title, action) ->
                SettingsItem(title, action)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    PianoMemoTheme {
        SettingsScreen()
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}