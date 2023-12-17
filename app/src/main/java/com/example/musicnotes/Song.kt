package com.example.musicnotes

data class Song(
    val id: String,
    val title: String,
    val videoUrl: String,
    val imageResourceId: Int
)

val allSongs = listOf(
    Song("1", "Адай", "video_url_1", R.drawable.adai),
    Song("2", "Балбырауын", "video_url_2", R.drawable.balb),
    Song("3", "Аққу", "video_url_3", R.drawable.akku),
    Song("4", "Ерке Сылқым", "video_url_4", R.drawable.erke),
    Song("5", "Көңіл толқыны", "video_url_5", R.drawable.konil),
)

fun getSongById(songId: String?): Song? {
    return allSongs.find { it.id == songId }
}

