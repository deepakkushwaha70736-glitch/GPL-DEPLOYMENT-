package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.RecentSearchEntity
import com.example.data.model.FilterCategory
import com.example.data.model.GplDeployment
import com.example.data.model.SearchMode
import com.example.ui.components.DeploymentCard
import com.example.ui.components.PoliceHeader
import com.example.ui.components.SearchFilterBar
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceNavy800

@Composable
fun LookupScreen(
    query: String,
    mode: SearchMode,
    selectedCategory: FilterCategory,
    searchResults: List<GplDeployment>,
    totalDeployments: Int,
    totalCodes: Int,
    isLoading: Boolean,
    recentSearches: List<RecentSearchEntity>,
    bookmarks: List<BookmarkEntity>,
    onQueryChanged: (String) -> Unit,
    onModeChanged: (SearchMode) -> Unit,
    onCategoryChanged: (FilterCategory) -> Unit,
    onClearSearch: () -> Unit,
    onRecentSearchClicked: (RecentSearchEntity) -> Unit,
    onClearRecentSearches: () -> Unit,
    onToggleBookmark: (GplDeployment) -> Unit,
    onViewDetail: (GplDeployment) -> Unit,
    onOpenAbout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val bookmarkedKeySet = bookmarks.map { it.recordKey }.toSet()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("lookup_screen")
    ) {
        // Top Police Header with Stats
        PoliceHeader(
            totalEntries = totalDeployments,
            totalCodes = totalCodes,
            currentMatches = searchResults.size,
            onOpenAbout = onOpenAbout
        )

        // Search & Filter Bar
        SearchFilterBar(
            query = query,
            mode = mode,
            selectedCategory = selectedCategory,
            recentSearches = recentSearches,
            onQueryChanged = onQueryChanged,
            onModeChanged = onModeChanged,
            onCategoryChanged = onCategoryChanged,
            onClearSearch = onClearSearch,
            onRecentSearchClicked = onRecentSearchClicked,
            onClearRecentSearches = onClearRecentSearches
        )

        // Results Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Deployment Results",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${searchResults.size} match${if (searchResults.size == 1) "" else "es"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PoliceBlue700
            )
        }

        // List or Empty/Loading State
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PoliceBlue700)
            }
        } else if (searchResults.isEmpty()) {
            EmptySearchState(
                query = query,
                mode = mode
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("search_results_list"),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = searchResults,
                    key = { it.uniqueKey }
                ) { deployment ->
                    DeploymentCard(
                        deployment = deployment,
                        searchQuery = query,
                        isBookmarked = bookmarkedKeySet.contains(deployment.uniqueKey),
                        onToggleBookmark = { onToggleBookmark(deployment) },
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            Toast.makeText(context, "Copied: $code", Toast.LENGTH_SHORT).show()
                        },
                        onCopySummary = {
                            val summary = buildDeploymentSummary(deployment)
                            clipboardManager.setText(AnnotatedString(summary))
                            Toast.makeText(context, "Copied deployment details", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val summary = buildDeploymentSummary(deployment)
                            shareText(context, summary, "GPL Deployment: ${deployment.location ?: "Record"}")
                        },
                        onViewDetail = { onViewDetail(deployment) }
                    )
                }

                item {
                    FooterNotice()
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(query: String, mode: SearchMode) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = if (query.isEmpty()) Icons.Default.Radio else Icons.Default.SearchOff,
                contentDescription = null,
                tint = PoliceNavy800.copy(alpha = 0.6f),
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (query.isEmpty()) {
                    "Search box में Set Number, ANI Number या थाना दर्ज करें"
                } else {
                    "कोई matching entry नहीं मिली"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (query.isEmpty()) {
                    "Search तुरंत update होगा। Set Number (C4110644), ANI (861029) या थाना नाम (Kotwali) से खोजें।"
                } else {
                    "Search mode (🔢 Set, 📞 ANI, 📍 जगह) बदलकर देखें या स्पेलिंग चेक करें।"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FooterNotice() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📻 Hathras Police GPL Wireless Telecommunication System",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
        )
        Text(
            text = "Developed by AO DEEPAK KUSHWAHA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = "Offline Embedded Dataset • Official NX3720 / NX3220 Roster",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

private fun buildDeploymentSummary(d: GplDeployment): String {
    return buildString {
        append("📍 Hathras Police Wireless Deployment\n")
        append("Location: ${d.location ?: "N/A"}\n")
        append("S.L. No: ${d.slNo} (PDF Page ${d.pdfPage})\n")
        if (d.equipmentCodes.isNotEmpty()) {
            append("Set Codes: ${d.equipmentCodes.joinToString(", ")}\n")
        }
        if (d.aniRecords.isNotEmpty()) {
            append("ANI Numbers: ${d.aniRecords.mapNotNull { it.ani }.joinToString(", ")}\n")
        }
        d.stMobSetNX3720?.let { append("NX3720 Base/Mob: $it\n") }
        d.hhSetNX3220?.let { append("NX3220 Handheld: $it\n") }
        d.powerSupply?.let { append("Power Supply: $it\n") }
        d.battery12V?.let { append("12V Battery: $it\n") }
        d.paSystem?.let { append("PA System: $it\n") }
        d.remarks?.let { append("Remarks: $it\n") }
    }
}

private fun shareText(context: Context, text: String, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    context.startActivity(shareIntent)
}
