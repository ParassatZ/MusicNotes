package com.example.musicnotes

data class Song(
    val id: String,
    val title: String,
    val videoUrl: String,
    val imageResourceId: Int
)

val allSongs = listOf(
    Song("1", "Song 1", "video_url_1", R.drawable.barca),
    Song("2", "Song 2", "video_url_2", R.drawable.barca),
    Song("3", "Song 3", "video_url_3", R.drawable.barca),
    Song("4", "Song 4", "video_url_4", R.drawable.barca),
    Song("5", "Song 5", "video_url_5", R.drawable.barca),
)

fun getSongById(songId: String?): Song? {
    return allSongs.find { it.id == songId }
}

