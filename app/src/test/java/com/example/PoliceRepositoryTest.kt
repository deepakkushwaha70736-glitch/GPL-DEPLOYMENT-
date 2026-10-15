package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BookmarkEntity
import com.example.data.local.PoliceDatabase
import com.example.data.model.FilterCategory
import com.example.data.model.SearchMode
import com.example.data.repository.PoliceRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PoliceRepositoryTest {

    private lateinit var database: PoliceDatabase
    private lateinit var repository: PoliceRepository
    private lateinit var context: Context

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PoliceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = PoliceRepository(context, database.policeDao())
        repository.initialize()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `verify gpl deployments and ani are loaded from assets`() {
        val totalEntries = repository.getTotalEntriesCount()
        val totalCodes = repository.getTotalUniqueCodesCount()

        assertTrue("Should have 121 GPL entries", totalEntries >= 100)
        assertTrue("Should have unique equipment codes", totalCodes >= 100)
    }

    @Test
    fun `search by set number returns matching deployment`() {
        // C4110626 is DM CAMP OFFICE
        val results = repository.searchDeployments("C4110626", SearchMode.SET, FilterCategory.ALL)
        assertTrue("Should find matching deployment for C4110626", results.isNotEmpty())
        assertEquals("DM CAMP OFFICE", results[0].location)
    }

    @Test
    fun `search by place name returns matching station`() {
        val results = repository.searchDeployments("KOTWALI", SearchMode.PLACE, FilterCategory.ALL)
        assertTrue("Should find Kotwali results", results.isNotEmpty())
        assertTrue(results.any { it.location?.contains("KOTWALI", ignoreCase = true) == true })
    }

    @Test
    fun `search by ANI number returns matching caller ID`() {
        val results = repository.searchDeployments("861010", SearchMode.ANI, FilterCategory.ALL)
        assertTrue("Should find ANI 861010", results.isNotEmpty())
    }

    @Test
    fun `bookmark insert and retrieval works`() = runBlocking {
        val results = repository.searchDeployments("DM CAMP", SearchMode.PLACE, FilterCategory.ALL)
        assertTrue(results.isNotEmpty())
        val item = results.first()

        repository.toggleBookmark(item)
        val isSaved = database.policeDao().isBookmarked(item.uniqueKey)
        assertTrue("Item should be bookmarked", isSaved)

        repository.updateBookmarkNote(item.uniqueKey, "Tested on duty")
        val bookmarks = database.policeDao().getAllBookmarks()
        // Toggle again to remove
        repository.toggleBookmark(item)
        val isRemoved = database.policeDao().isBookmarked(item.uniqueKey)
        assertTrue("Item should be removed from bookmarks", !isRemoved)
    }
}
