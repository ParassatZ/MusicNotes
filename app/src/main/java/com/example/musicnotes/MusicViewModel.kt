package com.example.musicnotes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {
    private val favoriteSongs = mutableStateOf<List<Song>>(emptyList())

    fun getFavoriteSongs(): List<Song> = favoriteSongs.value

    fun addToFavorites(song: Song?) {
        if (song != null) {
            favoriteSongs.value = favoriteSongs.value + listOf(song)
        }
    }

}

class SharedViewModel : ViewModel() {
    var enteredUsername: MutableState<String> = mutableStateOf("")
}
