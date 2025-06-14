package com.example.pianomemo.screen.confirm

import com.example.pianomemo.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pianomemo.data.local.MusicInfo
import com.example.pianomemo.ui.theme.PianoMemoTheme
import com.example.pianomemo.viewmodel.MusicInfoViewModel

@Composable
fun ConfirmScreen(viewModel: MusicInfoViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val musicInfoList = viewModel.musicInfo.collectAsState().value
    val playableMusicCount = remember { mutableIntStateOf(0) }
    val filteredList = if (searchQuery.isEmpty()) {
        musicInfoList
    } else {
        musicInfoList.filter { musicInfo ->
            listOf(
                musicInfo.nameOfMusic,
                musicInfo.nameOfArtist,
                musicInfo.nameOfJenre,
                musicInfo.nameOfStyle,
            ).any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        playableMusicCount.intValue = viewModel.getPlayableMusicCount()
    }

    if (musicInfoList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "まだデータが登録されていません。",
                fontSize = 18.sp,
                color = Color.Gray
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.space_16_dp)))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.space_16_dp)))
                Text("合計曲数：")
                Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.space_8_dp)))
                Text("${musicInfoList.size}")
                Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.space_24_dp)))
            }
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_16_dp)))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.space_16_dp)))
                Text("練習中の合計曲数：")
                Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.space_8_dp)))
                val practicingMusicList: Int = musicInfoList.size - playableMusicCount.value
                Text(practicingMusicList.toString())
                Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.space_32_dp)))

                Text("完璧に弾ける合計曲数：")
                Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.space_8_dp)))
                Text("${playableMusicCount.value}")
            }

            SearchScreen(
                searchQuery, { searchQuery = it }
            )

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "検索結果がありません。",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = filteredList ) { musicInfo ->
                        MusicItem(musicInfo = musicInfo, viewModel)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = musicInfoList) { musicInfo ->
                    MusicItem(musicInfo = musicInfo, viewModel)
                }
            }
        }
    }
}

@Composable
private fun MusicItem(
    musicInfo: MusicInfo,
    viewModel: MusicInfoViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val labelWidth = 120.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
            ) {
                Text(text = "曲名: ${musicInfo.nameOfMusic}")
                Text(text = "作曲者名: ${musicInfo.nameOfArtist}")
                Text(text = "ジャンル: ${musicInfo.nameOfJenre}")
                Text(text = "演奏スタイル: ${musicInfo.nameOfStyle}")
                Text(text = "メモ: ${musicInfo.nameOfMemo}")

                Spacer(modifier = Modifier.height(16.dp))

                // 右手の習熟度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "右手の習熟度: ${musicInfo.levelOfRightHand}",
                        modifier = Modifier.width(labelWidth)
                    )
                    Slider(
                        value = musicInfo.levelOfRightHand.toFloat(),
                        onValueChange = {},
                        enabled = false,
                        valueRange = 0f..100f,
                        steps = 0,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            disabledThumbColor = Color.Transparent,
                            disabledActiveTrackColor = Color(0xFF9C27B0),
                            disabledInactiveTrackColor = Color(0xff808080),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 左手の習熟度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "左手の習熟度: ${musicInfo.levelOfLeftHand}",
                        modifier = Modifier.width(labelWidth)
                    )
                    Slider(
                        value = musicInfo.levelOfLeftHand.toFloat(),
                        onValueChange = {},
                        enabled = false,
                        valueRange = 0f..100f,
                        steps = 0,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            disabledThumbColor = Color.Transparent,
                            disabledActiveTrackColor = Color(0xFF9C27B0),
                            disabledInactiveTrackColor = Color(0xff808080),
                        )
                    )
                }
            }

            Menu(
                modifier = Modifier.align(Alignment.CenterEnd),
                viewModel = viewModel,
                musicInfo = musicInfo,
                onEdit = { showEditDialog = true }
            )
        }
    }

    if (showEditDialog) {
        EditMusicDialog(
            musicInfo = musicInfo,
            viewModel = viewModel,
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    viewModel: MusicInfoViewModel,
    musicInfo: MusicInfo,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = {
                expanded = true
            }
        ) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "",
                tint = Color(0xFF9C27B0)
            )
        }
        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.onSurface),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onEdit()
                }
            ) {
                Text(
                    text = "編集",
                    fontSize = 24.sp,
                    color = Color.Gray,
                )
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    showDialog = true
                }
            ) {
                Text(
                    text = "削除",
                    fontSize = 24.sp,
                    color = Color.Gray,
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("削除確認") },
            text = { Text("このデータを削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMusicValues(musicInfo)
                        showDialog = false
                    }
                ) {
                    Text("削除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Composable
private fun EditMusicDialog(
    musicInfo: MusicInfo,
    viewModel: MusicInfoViewModel,
    onDismiss: () -> Unit
) {
    var nameOfMusic by remember { mutableStateOf(musicInfo.nameOfMusic) }
    var nameOfArtist by remember { mutableStateOf(musicInfo.nameOfArtist) }
    var nameOfJenre by remember { mutableStateOf(musicInfo.nameOfJenre) }
    var nameOfStyle by remember { mutableStateOf(musicInfo.nameOfStyle) }
    var nameOfMemo by remember { mutableStateOf(musicInfo.nameOfMemo) }
    var levelOfRightHand by remember { mutableStateOf(musicInfo.levelOfRightHand.toString()) }
    var levelOfLeftHand by remember { mutableStateOf(musicInfo.levelOfLeftHand.toString()) }
    var numOfRightHand by remember { mutableFloatStateOf(levelOfRightHand.toFloat()) }
    var numOfLeftHand by remember { mutableFloatStateOf(levelOfLeftHand.toFloat()) }
    val labelWidth = 120.dp

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("音楽データを編集") },
        text = {
            Column {
                OutlinedTextField(
                    value = nameOfMusic,
                    onValueChange = { nameOfMusic = it },
                    label = { Text("曲名") }
                )
                OutlinedTextField(
                    value = nameOfArtist,
                    onValueChange = { nameOfArtist = it },
                    label = { Text("作曲者名") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = nameOfJenre,
                    onValueChange = { nameOfJenre = it },
                    label = { Text("ジャンル") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = nameOfStyle,
                    onValueChange = { nameOfStyle = it },
                    label = { Text("演奏スタイル") }
                )
                OutlinedTextField(
                    value = nameOfMemo,
                    onValueChange = { nameOfMemo = it },
                    label = { Text("メモ") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 右手の習熟度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "右手の習熟度: ${numOfRightHand.toInt()}",
                        modifier = Modifier.width(labelWidth)
                    )
                    Slider(
                        value = numOfRightHand,
                        onValueChange = { numOfRightHand = it },
                        enabled = true,
                        valueRange = 0f..100f,
                        steps = 0,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            activeTickColor = Color.Transparent,
                            activeTrackColor = Color(0xFF9C27B0),
                            inactiveTrackColor = Color(0xff808080),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 左手の習熟度
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "左手の習熟度: ${numOfLeftHand.toInt()}",
                        modifier = Modifier.width(labelWidth)
                    )
                    Slider(
                        value = numOfLeftHand,
                        onValueChange = { numOfLeftHand = it },
                        enabled = true,
                        valueRange = 0f..100f,
                        steps = 0,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            activeTickColor = Color.Transparent,
                            activeTrackColor = Color(0xFF9C27B0),
                            inactiveTrackColor = Color(0xff808080),
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateMusicValues(
                        musicInfo.copy(
                            nameOfMusic = nameOfMusic,
                            nameOfArtist = nameOfArtist,
                            nameOfJenre = nameOfJenre,
                            nameOfStyle = nameOfStyle,
                            nameOfMemo = nameOfMemo,
                            levelOfRightHand = numOfRightHand.toInt(),
                            levelOfLeftHand = numOfLeftHand.toInt()
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF9C27B0),
                    contentColor = Color.White
                )
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFB0BEC5),
                    contentColor = Color.White
                )
            ) {
                Text("キャンセル")
            }
        }
    )
}

@Composable
private fun NoRecordImageView(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.music_note),
        contentDescription = stringResource(id = R.string.description_of_the_image),
        modifier = modifier
            .size((dimensionResource(id = R.dimen.no_record_image_view)))
            .aspectRatio(1f)
    )
}

@Preview(showBackground = true)
@Composable
private fun NoRecordImageViewPreview() {
    PianoMemoTheme {
        NoRecordImageView(modifier = Modifier)
    }
}

@Composable
private fun NoRecordText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.no_record),
        fontWeight = FontWeight.Bold,
        fontSize = dimensionResource(
            id = R.dimen.no_record_text_font_size
        ).value.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun NoRecordTextPreview() {
    PianoMemoTheme {
        NoRecordText()
    }
}

@Composable
private fun NoRecordDescriptionText(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.no_record_description),
        fontWeight = FontWeight.Light,
        color = Color.LightGray
    )
}

@Preview(showBackground = true)
@Composable
private fun NoRecordDescriptionTextPreview() {
    PianoMemoTheme {
        NoRecordDescriptionText()
    }
}

@Composable
private fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_16_dp))) {
        SearchBar(query = query, onQueryChange = onQueryChange)
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val modifier = Modifier
        .fillMaxWidth()
        .background(
            Color.Gray.copy(alpha = 0.1f),
            shape = RoundedCornerShape(
                dimensionResource(
                    id = R.dimen.search_bar_background_rounded_corner_shape
                )
            )
        )
        .padding(dimensionResource(id = R.dimen.space_8_dp))

    Box(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    stringResource(id = R.string.search_by_text)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(
                        id = R.string.content_description_search_icon
                    )
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}