package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val recordKey: String,
    val location: String,
    val slNo: Int,
    val pdfPage: String,
    val setNumbers: String,
    val aniNumbers: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String,
    val mode: String,
    val timestamp: Long = System.currentTimeMillis()
)
