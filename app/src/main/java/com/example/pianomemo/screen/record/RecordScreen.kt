package com.example.pianomemo.screen.record

import com.example.pianomemo.R
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pianomemo.data.remote.Artist
import com.example.pianomemo.data.remote.SpotifyApiService
import com.example.pianomemo.data.remote.Track
import com.example.pianomemo.viewmodel.MusicInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: MusicInfoViewModel,
    retrofitService: SpotifyApiService,
    authToken: String
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var textOfMusic by remember { mutableStateOf("") }
    var textOfArtist by remember { mutableStateOf("") }
    var textOfGenre by remember { mutableStateOf("") }
    var textOfStyle by remember { mutableStateOf("") }
    var textOfMemo by remember { mutableStateOf("") }
    var numOfRightHand by remember { mutableFloatStateOf(0F) }
    var numOfLeftHand by remember { mutableFloatStateOf(0F) }
    var suggestedMusic by remember { mutableStateOf<List<Track>>(emptyList()) }
    var suggestedArtists by remember { mutableStateOf<List<Artist>>(emptyList()) }
    var isArtistsSuggestionVisible by remember { mutableStateOf(false) }
    var isMusicSuggestionVisible by remember { mutableStateOf(false) }
    val musicFieldOffset = remember { mutableStateOf(Offset.Zero) }
    val artistFieldOffset = remember { mutableStateOf(Offset.Zero) }
    var musicFieldHeight by remember { mutableIntStateOf(60) }
    var artistFieldHeight by remember { mutableIntStateOf(120) }

    val musicFieldModifier = Modifier
        .onGloballyPositioned { coordinates ->
            musicFieldOffset.value = coordinates.positionInRoot()
            musicFieldHeight = coordinates.size.height
        }

    val artistFieldModifier = Modifier
        .onGloballyPositioned { coordinates ->
            artistFieldOffset.value = coordinates.positionInRoot()
            artistFieldHeight = coordinates.size.height
        }

    LaunchedEffect(textOfMusic) {
        val isMusicInSuggestions = suggestedMusic.any { it.name == textOfMusic }
        if (textOfMusic.isNotBlank()  && !isMusicInSuggestions) {
            suggestedMusic = fetchMusicSuggestions(
                textOfMusic,
                authToken,
                retrofitService
            )
            isMusicSuggestionVisible = suggestedMusic.isNotEmpty()
        } else {
            suggestedMusic = emptyList()
            isMusicSuggestionVisible = false
        }
    }

    LaunchedEffect(textOfArtist) {
        val isArtistInSuggestions = suggestedArtists.any { it.name == textOfArtist }
        if (textOfArtist.isNotBlank() && !isArtistInSuggestions) {
            suggestedArtists = fetchArtistSuggestions(
                textOfArtist,
                authToken,
                retrofitService
            )
            isArtistsSuggestionVisible = suggestedArtists.isNotEmpty()
        } else {
            suggestedArtists = emptyList()
            isArtistsSuggestionVisible = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "記録",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
                .padding(dimensionResource(id = R.dimen.space_16_dp))
        ) {
            Column {
                MusicOutlinedTextField(
                    label = stringResource(id = R.string.music_name),
                    placeholder = stringResource(id = R.string.placeholder_music),
                    value = textOfMusic,
                    onValueChange = { textOfMusic = it },
                    modifier = musicFieldModifier
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_8_dp)))

                MusicOutlinedTextField(
                    label = stringResource(id = R.string.artist_name),
                    placeholder = stringResource(id = R.string.placeholder_artist),
                    value = textOfArtist,
                    onValueChange = { textOfArtist = it },
                    modifier = artistFieldModifier
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_8_dp)))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensionResource(id = R.dimen.space_16_dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ジャンル")
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_16_dp)))
                        DropdownMenuWithIcon(
                            modifier = Modifier.weight(1f),
                            items = listOf("クラシック", "ジャズ", "ポップス", "ロック", "その他"),
                            value = textOfGenre,
                            onValueChange = { textOfGenre = it },
                        )
                    }
                    Row (
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "演奏スタイル")
                        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_16_dp)))
                        DropdownMenuWithIcon(
                            modifier = Modifier.weight(1f),
                            items = listOf("独奏", "連弾", "伴奏", "弾き語り"),
                            value = textOfStyle,
                            onValueChange = { textOfStyle = it }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_8_dp)))
                MusicOutlinedTextField(
                    label = stringResource(id = R.string.memo_name),
                    placeholder = stringResource(id = R.string.placeholder_memo),
                    value = textOfMemo,
                    onValueChange = { textOfMemo = it },
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_16_dp)))
                ProgressSection(stringResource(id = R.string.right_hand)) {
                    CircularProgressWithSeekBar(
                        value = numOfRightHand,
                        onValueChange = { numOfRightHand = it }
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_16_dp)))

                ProgressSection(stringResource(id = R.string.left_hand)) {
                    CircularProgressWithSeekBar(
                        value = numOfLeftHand,
                        onValueChange = { numOfLeftHand = it }
                    )
                }

                val isButtonEnabled = textOfMusic.isNotBlank()
                        && textOfArtist.isNotBlank()
                        && textOfGenre.isNotBlank()
                        && textOfStyle.isNotBlank()
                        && textOfMemo.isNotBlank()
                        && numOfRightHand > 1
                        && numOfLeftHand > 1
                var showToast by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SaveButton(
                            onClick = {
                                viewModel.saveValues(
                                    textOfMusic,
                                    textOfArtist,
                                    textOfGenre,
                                    textOfStyle,
                                    textOfMemo,
                                    numOfRightHand,
                                    numOfLeftHand
                                )
                            },
                            enabled = isButtonEnabled,
                            onShowToast = { showToast = true }
                        )
                    }
                    PianoToast(
                        message = "記録しました",
                        imageResId = R.drawable.music_note,
                        visible = showToast,
                        onDismiss = { showToast = false }
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_16_dp)))
            }

            if (isMusicSuggestionVisible && suggestedMusic.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp)
                        .align(Alignment.TopStart)
                        .offset(
                            x = musicFieldOffset.value.x.dp,
                            y = (musicFieldOffset.value.y + musicFieldHeight).dp
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                ) {
                    items(suggestedMusic) { music ->
                        Text(
                            text = music.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textOfMusic = music.name
                                    isMusicSuggestionVisible = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }

            if (isArtistsSuggestionVisible && suggestedArtists.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp)
                        .align(Alignment.TopStart)
                        .offset(
                            x = artistFieldOffset.value.x.dp,
                            y = (artistFieldOffset.value.y + artistFieldHeight).dp
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                ) {
                    items(suggestedArtists) { artist ->
                        Text(
                            text = artist.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textOfArtist = artist.name
                                    isArtistsSuggestionVisible = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicOutlinedTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(0.18f))
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.space_16_dp)))
        androidx.compose.material.OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(id = R.dimen.text_field_height)),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(fontSize = dimensionResource(id = R.dimen.text_size_normal).value.sp),
                    color = Color.Gray
                )
            },
            shape = RoundedCornerShape(10),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            )
        )
    }
}

suspend fun fetchArtistSuggestions(
    query: String,
    authToken: String,
    retrofitService: SpotifyApiService
): List<Artist> {
    return withContext(Dispatchers.IO) {
        try {
            val response = retrofitService.searchMusic(
                query = query,
                type = "artist",
                authHeader = "Bearer $authToken"
            )

            if (response.isSuccessful) {
                val spotifySearchResponse = response.body()
                spotifySearchResponse?.artists?.items ?: emptyList()
            } else {
                println("Error fetching artist suggestions: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Exception fetching artist suggestions: ${e.message}")
            emptyList()
        }
    }
}

suspend fun fetchMusicSuggestions(
    query: String,
    authToken: String,
    retrofitService: SpotifyApiService
): List<Track> {
    return withContext(Dispatchers.IO) {
        try {
            val response = retrofitService.searchMusic(
                query = query,
                type = "track",
                authHeader = "Bearer $authToken"
            )

            if (response.isSuccessful) {
                val spotifySearchResponse = response.body()
                spotifySearchResponse?.tracks?.items ?: emptyList()
            } else {
                println("Error fetching artist suggestions: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Exception fetching artist suggestions: ${e.message}")
            emptyList()
        }
    }
}

@Composable
private fun DropdownMenuWithIcon(
    modifier: Modifier,
    items: List<String>,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.wrapContentSize(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 8.dp)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onValueChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(
    label: String,
    progressContent: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 20.sp)
        progressContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CircularProgressWithSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(dimensionResource(id = R.dimen.circular_progress_with_seek_bar_size))
    ) {
        CircularProgressIndicator(
            color = Color(0xff8a2be2),
            strokeWidth = dimensionResource(id = R.dimen.circular_progress_indicator_stroke_width),
            progress = value / 100,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.circular_progress_with_seek_bar_size))
                .padding(dimensionResource(id = R.dimen.space_8_dp))
        )
        Text(
            text = "${value.toInt()}%",
            style = TextStyle(fontSize = dimensionResource(id = R.dimen.text_size_large).value.sp),
        )
    }
    Slider(
        colors = SliderDefaults.colors(
            activeTrackColor = Color(0xff8a2be2),
            inactiveTrackColor = Color.Gray,
            thumbColor = Color.Gray
        ),
        value = value,
        onValueChange = onValueChange,
        valueRange = 0f..100f,
        thumb = {
            Canvas(Modifier.size(8.dp)) {
                drawCircle(Color(0xff8a2be2))
            }
        }
    )
}

@Composable
private fun SaveButton(
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    onShowToast: () -> Unit = {}
) {
    Button(
        onClick = {
            onClick()
            onShowToast()
        },
        colors = ButtonDefaults.buttonColors(Color.Blue),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled,
    ) {
        Text(stringResource(id = R.string.record_button))
    }
}

private fun showToast(
    context: android.content.Context,
    message: String
) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun PianoToast(
    message: String,
    imageResId: Int,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        LaunchedEffect(Unit) {
            delay(2000)
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.wrapContentSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Piano",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = message)
                }
            }
        }
    }
}
