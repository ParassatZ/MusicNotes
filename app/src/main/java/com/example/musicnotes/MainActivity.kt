@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNUSED_EXPRESSION", "UNUSED_EXPRESSION")

package com.example.musicnotes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicnotes.ui.theme.MusicNotesTheme
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    private val musicViewModel: MusicViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicNotesTheme {
                AppNavigation(musicViewModel, sharedViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(musicViewModel: MusicViewModel, sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    val favoriteSongsState = remember { mutableStateOf<List<Song>>(emptyList()) }
    val registrationData = remember { mutableStateOf(RegistrationData("", "")) }
    NavHost(
        navController = navController,
        startDestination = "register"
    ) {
        composable("register") { RegistrationScreen(navController, registrationData) }
        composable("login") { LoginScreen(navController = navController, registrationData.value, sharedViewModel) }
        composable("main") { MainScreen(navController, sharedViewModel) }
        composable("songList") {
            SongListScreen(
                navController,
                songs = emptyList(),
                navigateToSong = { song -> navController.navigate("songDetail/${song.id}") })
        }
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
        composable("profile/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            ProfileScreen(navController, sharedViewModel)
        }
    }
}

fun addToFavorites(song: Song?, favoriteSongsState: MutableState<List<Song>>) {
    if (song != null) {
        favoriteSongsState.value = favoriteSongsState.value + listOf(song)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController, viewModel: SharedViewModel) {
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
                        title = { Text("Music Notes", modifier = Modifier
                            .padding(start = 32.dp),
                            textAlign = TextAlign.Center,
                            color = Color.hsl(0.61F, 0.51F, 0.16F)
                        ) },
                        Modifier.background(color = MaterialTheme.colorScheme.primary)
                    )
                },
                content = {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .height(200.dp)
                                .width(200.dp)
                                .clip(shape = MaterialTheme.shapes.medium)
                        )
                        Button(
                            onClick = {
                                navController.navigate("songList") {

                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .background(Color.hsl(0.61F, 0.51F, 0.16F), shape = RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(Color.hsl(0.61F, 0.51F, 0.16F)

                            )
                        ) {
                            Text(
                                text = "Start",
                                style = TextStyle(fontSize = 20.sp)
                            )
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
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.hsl(37.28F, 0.98F, 0.53F))
            }
            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.hsl(37.28F, 0.98F, 0.53F))
            }

            IconButton(onClick = {val username = viewModel.enteredUsername.value
                    navController.navigate("profile/$username")}) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.hsl(37.28F, 0.98F, 0.53F))
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SongListScreen(
    navController: NavHostController,
    songs: List<Song>,
    navigateToSong: (Song) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Song List", color = Color.hsl(0.61F, 0.51F, 0.16F)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 48.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {
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
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium, color = Color.hsl(0.61F, 0.51F, 0.16F))
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
) {
    val song = songs.find { it!!.id == songId }
    if (song == null) {
        Text("Song not found")
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(song?.title ?: "Song Detail", color = Color.hsl(0.61F, 0.51F, 0.16F)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("songList") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { musicViewModel.addToFavorites(song) }) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.hsl(37.28F, 0.98F, 0.53F))
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
                Text(
                    text = song?.title ?: "Song Detail",
                    style = MaterialTheme.typography.headlineMedium,
                            color = Color.hsl(0.61F, 0.51F, 0.16F)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("Video Tutorial: ")
                        }
                        append(song?.videoUrl ?: "")
                    },
                    modifier = Modifier.clickable {
                        openLinkInBrowser(context, song?.videoUrl ?: "")
                    },color = Color.hsl(0.61F, 0.51F, 0.16F)


                )
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

fun openLinkInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun SongDetailScreenPreview() {
    val song = Song("1", "Sample Song", "sample_video_url", R.drawable.barca)

    MusicNotesTheme {
        SongDetailScreen(
            songId = "1",
            navController = rememberNavController(),
            songs = listOf(song),
            musicViewModel = MusicViewModel()
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavHostController, musicViewModel: MusicViewModel) {
    val favoriteSongs = musicViewModel.getFavoriteSongs()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", color = Color.hsl(0.61F, 0.51F, 0.16F)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
                if (favoriteSongs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .height(200.dp)
                                .width(200.dp)
                                .clip(shape = MaterialTheme.shapes.medium)
                        )
                        Text("The favorites list is empty", textAlign = TextAlign.Center, color = Color.hsl(0.61F, 0.51F, 0.16F), style = MaterialTheme.typography.headlineMedium)
                    }
                }
                     else {

                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 48.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                        ) {
                            items(favoriteSongs) { song ->
                                FavoriteSongItem(song = song)
                                Divider()
                            }
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
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium, color = Color.hsl(0.61F, 0.51F, 0.16F))
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
fun SearchScreen(
    navController: NavHostController,
    allSongs: List<Song>,
    onSearch: (String) -> Unit
) {
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
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 48.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {
                items(items = com.example.musicnotes.allSongs.filter {
                    it.title.contains(
                        searchText.text,
                        ignoreCase = true
                    )
                }) { song ->
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
            Text(text = song.title, style = MaterialTheme.typography.headlineMedium, color = Color.hsl(0.61F, 0.51F, 0.16F))
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
        Song("1", "Search Song 1", "search_video_url_1", R.drawable.ic_launcher_foreground),
        Song("2", "Search Song 2", "search_video_url_2", R.drawable.ic_launcher_foreground),
        Song("3", "Search Song 3", "search_video_url_3", R.drawable.ic_launcher_foreground),
        Song("4", "Search Song 4", "search_video_url_4", R.drawable.ic_launcher_foreground),
        Song("5", "Search Song 5", "search_video_url_5", R.drawable.ic_launcher_foreground),
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
        MainScreen(navController = navController, viewModel = SharedViewModel())
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavHostController, registrationData: RegistrationData, viewModel: SharedViewModel) {
    var enteredUsername by remember { mutableStateOf(TextFieldValue()) }
    var enteredPassword by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login", color = Color.hsl(0.61F, 0.51F, 0.16F)) }
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
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                )
                TextField(
                    value = viewModel.enteredUsername.value,
                    onValueChange = {
                        viewModel.enteredUsername.value = it
                    },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = enteredPassword,
                    onValueChange = { enteredPassword = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (viewModel.enteredUsername.value == registrationData.username &&
                        enteredPassword.text == registrationData.password
                    ) {
                        navController.navigate("main") {
                            launchSingleTop = true
                        }
                    } else {
                    }
                },
                    modifier = Modifier.
                    background(Color.hsl(0.61F, 0.51F, 0.16F), shape = RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(Color.hsl(0.61F, 0.51F, 0.16F)

                    )
                    ) {
                    Text("Login")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    val registrationData = remember { mutableStateOf(RegistrationData("sample_username", "sample_password")) }
    val viewModel = remember { SharedViewModel() }

    MusicNotesTheme {
        LoginScreen(navController = navController, registrationData = registrationData.value, viewModel = viewModel)
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(
    navController: NavHostController,
    registrationData: MutableState<RegistrationData>
) {
    var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registration", color = Color.hsl(0.61F, 0.51F, 0.16F)) }
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
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (password.text == confirmPassword.text) {
                        registrationData.value = RegistrationData(username.text, password.text)
                        navController.navigate("login")
                    } else {
                    }
                },
                    modifier = Modifier.background(Color.hsl(0.61F, 0.51F, 0.16F), shape = RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(Color.hsl(0.61F, 0.51F, 0.16F)

                    )
                    ) {
                    Text("Register")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    val navController = rememberNavController()
    val registrationData = remember { mutableStateOf(RegistrationData("", "")) }

    MusicNotesTheme {
        RegistrationScreen(navController = navController, registrationData = registrationData)
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: SharedViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.hsl(0.61F, 0.51F, 0.16F)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                )
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .clip(shape = MaterialTheme.shapes.medium)
                )
                val username = viewModel.enteredUsername.value
                Text("Username: $username", style = MaterialTheme.typography.headlineMedium, color = Color.hsl(0.61F, 0.51F, 0.16F))

            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    val viewModel = SharedViewModel()

    MusicNotesTheme {
        ProfileScreen(navController = navController, viewModel = viewModel)
    }
}
