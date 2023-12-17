@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.musicnotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicNotesTheme {
                // Set up the main activity layout
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainScreen(navController) }
        composable("songList") { SongListScreen(navController, songs = emptyList(), navigateToSong = { song -> navController.navigate("songDetail/${song.id}") } ) }
        composable(
            route = "songDetail/{songId}",
            arguments = listOf(navArgument("songId") { type = NavType.StringType })
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            val song = getSongById(songId)
            SongDetailScreen(songId, navController, songs = listOf(song)) {}
        }
        composable("favorites") { FavoritesScreen(navController, favoriteSongs = emptyList()) }
        composable("search") { SearchScreen(navController, allSongs = emptyList(), onSearch = {}) }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Music Notes") },
                Modifier.background(color = MaterialTheme.colorScheme.primary)
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
                Button(onClick = { /* Handle Start button click */ }) {
                    Text("Start")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { /* Handle Favorites button click */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    }
                    IconButton(onClick = { /* Handle Search button click */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            }
        }
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongListScreen(navController: NavHostController, songs: List<Song>, navigateToSong: (Song) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Song List") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back button click */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            LazyColumn {
                items(songs) { song ->
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
        // Display song details (title and image)
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
    val songs = listOf(
        Song("1","Song 1", "video_url_1", R.drawable.barca),
        Song("2", "Song 2", "video_url_2", R.drawable.barca),
        Song("3", "Song 3", "video_url_3", R.drawable.barca),
        Song("4", "Song 4", "video_url_4", R.drawable.barca),
        Song("5","Song 5", "video_url_5", R.drawable.barca),
    )
    val navController = rememberNavController()
    MusicNotesTheme {
        SongListScreen(navController = navController, songs = songs, navigateToSong = {})
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongDetailScreen(
    songId: String?,
    navController: NavHostController,
    songs: List<Song?>,
    addToFavorites: () -> Unit
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
                    IconButton(onClick = { /* Handle back button click */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    // Add to Favorites button in the top app bar
                    IconButton(onClick = addToFavorites) {
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
                // Display song details (title, video, and image)
                Text(text = song?.title ?: "Song Detail", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Video Tutorial: ${song?.videoUrl}")
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = song?.imageResourceId ?: R.drawable.barca),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
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
        SongDetailScreen(songId = "1", navController = rememberNavController(), songs = listOf(song), addToFavorites = {})
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