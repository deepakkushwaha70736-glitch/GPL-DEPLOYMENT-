package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PoliceDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE recordKey = :key)")
    fun isBookmarkedFlow(key: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE recordKey = :key)")
    suspend fun isBookmarked(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE recordKey = :key")
    suspend fun deleteBookmark(key: String)

    @Query("UPDATE bookmarks SET note = :note WHERE recordKey = :key")
    suspend fun updateNote(key: String, note: String)

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 8")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE query = :query AND mode = :mode")
    suspend fun deleteRecentSearch(query: String, mode: String)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()
}
