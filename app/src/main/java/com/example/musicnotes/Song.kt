package com.example.musicnotes

data class Song(
    val id: String,
    val title: String,
    val videoUrl: String,
    val imageResourceId: Int
)

val allSongs = listOf(
    Song("1", "Adai","https://youtu.be/OOEEhHNLosI?si=ZBgrRo4sonLQhhiq", R.drawable.adai),
    Song("2", "Balbyrauyn", "https://youtu.be/tnntweIJO3E?si=22eypqY0zJMuLa7p", R.drawable.balb),
    Song("3", "Erke Sylkym", "https://youtu.be/ct_dzDqrgFw?si=PF8en2byCYp8psZR", R.drawable.erke),
    Song("4", "Akku","https://youtu.be/iSYIyENgzKQ?si=Fd1wMQ7rHNrp6eNP", R.drawable.akku),
    Song("5", "Konil Tolkyny", "https://youtu.be/9xupi32LzSw?si=6V8nGrlq58R6dVFQ", R.drawable.konil),
)

fun getSongById(songId: String?): Song? {
    return allSongs.find { it.id == songId }
}

