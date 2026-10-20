package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Radio
import com.example.ui.screens.AboutUsScreen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GplDeployment
import com.example.ui.MainViewModel
import com.example.ui.components.DeploymentDetailBottomSheet
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.LookupScreen
import com.example.ui.screens.RadioGuideScreen
import com.example.ui.theme.HathrasPoliceTheme
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HathrasPoliceTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
                val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }
                val context = LocalContext.current
                val clipboardManager = LocalClipboardManager.current

                // Handle Back Button
                BackHandler(enabled = uiState.selectedDeploymentForDetail != null || uiState.activeTab != 0) {
                    if (uiState.selectedDeploymentForDetail != null) {
                        viewModel.onSelectDeployment(null)
                    } else if (uiState.activeTab != 0) {
                        viewModel.onTabSelected(0)
                    }
                }

                // Show Snackbar when message is emitted
                LaunchedEffect(uiState.snackbarMessage) {
                    uiState.snackbarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.dismissSnackbar()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            val items = listOf(
                                Triple(0, "Lookup", Icons.Default.Radio),
                                Triple(1, "Directory", Icons.Default.Phone),
                                Triple(2, "About Us", Icons.Default.LocalPolice),
                                Triple(3, "Saved", Icons.Default.Bookmark),
                                Triple(4, "Radio Guide", Icons.Default.MenuBook)
                            )

                            items.forEach { (index, title, icon) ->
                                val isSelected = uiState.activeTab == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.onTabSelected(index) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = PoliceNavy800,
                                        indicatorColor = PoliceNavy800,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_item_$index")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.activeTab) {
                            0 -> LookupScreen(
                                query = uiState.searchQuery,
                                mode = uiState.searchMode,
                                selectedCategory = uiState.selectedCategory,
                                searchResults = uiState.searchResults,
                                totalDeployments = uiState.totalDeployments,
                                totalCodes = uiState.totalEquipmentCodes,
                                isLoading = uiState.isLoading,
                                recentSearches = recentSearches,
                                bookmarks = bookmarks,
                                onQueryChanged = viewModel::onQueryChanged,
                                onModeChanged = viewModel::onModeChanged,
                                onCategoryChanged = viewModel::onCategoryChanged,
                                onClearSearch = viewModel::clearSearch,
                                onRecentSearchClicked = viewModel::onRecentSearchClicked,
                                onClearRecentSearches = viewModel::clearRecentSearches,
                                onToggleBookmark = viewModel::toggleBookmark,
                                onViewDetail = viewModel::onSelectDeployment,
                                onOpenAbout = { viewModel.onTabSelected(2) }
                            )

                            1 -> DirectoryScreen(
                                contacts = viewModel.directoryContacts,
                                stationDistances = viewModel.stationDistancesHeights
                            )

                            2 -> AboutUsScreen(
                                onNavigateToDirectory = { viewModel.onTabSelected(1) }
                            )

                            3 -> BookmarksScreen(
                                bookmarks = bookmarks,
                                noteDialogItem = uiState.noteDialogItem,
                                onOpenNoteDialog = viewModel::openNoteDialog,
                                onCloseNoteDialog = viewModel::closeNoteDialog,
                                onSaveNote = viewModel::saveNote,
                                onDeleteBookmark = viewModel::deleteBookmark
                            )

                            4 -> RadioGuideScreen(
                                radioGuideItems = viewModel.radioGuideItems
                            )
                        }
                    }
                }

                // Modal Bottom Sheet for Deployment Detail
                uiState.selectedDeploymentForDetail?.let { deployment ->
                    val isBookmarked = bookmarks.any { it.recordKey == deployment.uniqueKey }
                    DeploymentDetailBottomSheet(
                        deployment = deployment,
                        isBookmarked = isBookmarked,
                        onDismiss = { viewModel.onSelectDeployment(null) },
                        onToggleBookmark = { viewModel.toggleBookmark(deployment) },
                        onCopyCode = { code ->
                            clipboardManager.setText(AnnotatedString(code))
                            Toast.makeText(context, "Copied: $code", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val summary = buildDeploymentSummary(deployment)
                            shareDeploymentText(context, summary, deployment.location ?: "Deployment Record")
                        },
                        onDialOrCopyAni = { ani ->
                            clipboardManager.setText(AnnotatedString(ani))
                            Toast.makeText(context, "Copied ANI: $ani", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
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
            d.distanceFromDcr?.let { append("Distance from DCR: $it\n") }
            d.antennaHeightFeet?.let { append("Antenna Height: $it\n") }
            d.powerSupply?.let { append("Power Supply: $it\n") }
            d.battery12V?.let { append("12V Battery: $it\n") }
            d.paSystem?.let { append("PA System: $it\n") }
            d.remarks?.let { append("Remarks: $it\n") }
        }
    }

    private fun shareDeploymentText(context: Context, text: String, title: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
