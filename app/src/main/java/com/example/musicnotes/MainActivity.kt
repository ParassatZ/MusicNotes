@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNUSED_EXPRESSION", "UNUSED_EXPRESSION")

package com.example.musicnotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicnotes.ui.theme.MusicNotesTheme
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    private val musicViewModel: MusicViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicNotesTheme {
                AppNavigation(musicViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(musicViewModel: MusicViewModel) {
    val navController = rememberNavController()
    val favoriteSongsState = remember { mutableStateOf<List<Song>>(emptyList()) }
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainScreen(navController)}
        composable("songList") { SongListScreen(navController, songs = emptyList(), navigateToSong = { song -> navController.navigate("songDetail/${song.id}") } ) }
        composable(
            route = "songDetail/{songId}",
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            val song = getSongById(songId)
            SongDetailScreen(songId, navController, songs = allSongs, musicViewModel)
        }
        composable("favorites") { FavoritesScreen(navController, musicViewModel) }
        composable("search") { SearchScreen(navController, allSongs = emptyList(), onSearch = {}) }
    }
}

fun addToFavorites(song: Song?, favoriteSongsState: MutableState<List<Song>>) {
    if (song != null) {
        favoriteSongsState.value = favoriteSongsState.value + listOf(song)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Music Notes") },
                        Modifier.background(color = MaterialTheme.colorScheme.primary)
                    )
                },
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            navController.navigate("songList") {

                            }
                        }) {
                            Text("Start")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.navigate("favorites") }) {
                Icon(Icons.Default.Favorite, contentDescription = null)
            }
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongListScreen(navController: NavHostController, songs: List<Song>, navigateToSong: (Song) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Song List") },
                navigationIcon = {
                    IconButton(onClick = {  navController.navigate("main")  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn( contentPadding = PaddingValues(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 16.dp)) {
                items(items = allSongs, key = { song -> song.id }) { song ->
                    SongListItem(song = song, navigateToSong = navigateToSong)
                    Divider()
                }
            }
        }
    )
}

@Composable
fun SongListItem(song: Song, navigateToSong: (Song) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToSong(song) }
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = song.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListScreenPreview() {

    val navController = rememberNavController()
    MusicNotesTheme {
        SongListScreen(navController = navController, songs = allSongs, navigateToSong = {})
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongDetailScreen(
    songId: String?,
    navController: NavHostController,
    songs: List<Song?>,
    musicViewModel: MusicViewModel
){
    val song = songs.find { it!!.id == songId }
    if (song==null){
        Text("Song not found")
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "Song Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("songList") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { musicViewModel.addToFavorites(song) }) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = song?.title ?: "Song Detail", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Video Tutorial: ${song?.videoUrl}")
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = song?.imageResourceId ?: R.drawable.barca),
                    contentDescription = null,
                    modifier = Modifier
                        .height(700.dp)
                        .width(700.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SongDetailScreenPreview() {
    val song = Song("1","Sample Song", "sample_video_url", R.drawable.barca)

    MusicNotesTheme {
        SongDetailScreen(songId = "1", navController = rememberNavController(), songs = listOf(song), musicViewModel = MusicViewModel())
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavHostController, musicViewModel: MusicViewModel) {
    val favoriteSongs = musicViewModel.getFavoriteSongs()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 16.dp)) {
                items(favoriteSongs) { song ->
                    FavoriteSongItem(song = song)
                    Divider()
                }
            }
        }
    )
}


@Composable
fun FavoriteSongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = song.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val favoriteSongs = listOf(
        Song("1", "Favorite Song 1", "favorite_video_url_1", R.drawable.ic_launcher_foreground),
        Song("2", "Favorite Song 2", "favorite_video_url_2", R.drawable.ic_launcher_foreground),
        Song("3", "Favorite Song 3", "favorite_video_url_3", R.drawable.ic_launcher_foreground),
    )
    val navController = rememberNavController()

    MusicNotesTheme {
        FavoritesScreen(navController = navController, musicViewModel = MusicViewModel())
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavHostController, allSongs: List<Song>, onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            onSearch(it.text)
                        },
                        placeholder = { Text("Search") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, top = 48.dp, end = 16.dp, bottom = 16.dp)) {
                items(items = com.example.musicnotes.allSongs.filter { it.title.contains(searchText.text, ignoreCase = true) }) { song ->
                    SearchSongItem(song = song)
                    Divider()
                }
            }
        }
    )
}

@Composable
fun SearchSongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = painterResource(id = song.imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val allSongs = listOf(
        Song("1","Search Song 1", "search_video_url_1", R.drawable.ic_launcher_foreground),
        Song("2", "Search Song 2", "search_video_url_2", R.drawable.ic_launcher_foreground),
        Song("3","Search Song 3", "search_video_url_3", R.drawable.ic_launcher_foreground),
        Song("4","Search Song 4", "search_video_url_4", R.drawable.ic_launcher_foreground),
        Song("5","Search Song 5", "search_video_url_5", R.drawable.ic_launcher_foreground),
    )
    val navController = rememberNavController()

    MusicNotesTheme {
        SearchScreen(navController = navController, allSongs = allSongs, onSearch = {})
    }
}




@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MusicNotesTheme {
        MainScreen(navController = navController)
    }
}