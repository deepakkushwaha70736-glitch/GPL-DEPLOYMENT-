package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.PoliceBlue100
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.RadioRed

@Composable
fun BookmarksScreen(
    bookmarks: List<BookmarkEntity>,
    noteDialogItem: BookmarkEntity?,
    onOpenNoteDialog: (BookmarkEntity) -> Unit,
    onCloseNoteDialog: () -> Unit,
    onSaveNote: (String, String) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("bookmarks_screen")
    ) {
        // Bookmarks Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PoliceNavy900, PoliceNavy800)
                    )
                )
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PoliceGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = PoliceGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "⭐ Saved Sets & Stations",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${bookmarks.size} Bookmarked item${if (bookmarks.size == 1) "" else "s"} with custom duty notes",
                        fontSize = 12.sp,
                        color = PoliceBlue100.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "कोई Saved Entry नहीं है",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Lookup टैब में किसी भी Set या थाना के कार्ड पर Bookmark (⭐) बटन दबाकर सेव करें। आप प्रत्येक सेट पर व्यक्तिगत ड्यूटी नोट्स भी लिख सकते हैं।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("bookmarks_list"),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = bookmarks,
                    key = { it.recordKey }
                ) { bookmark ->
                    BookmarkItemCard(
                        bookmark = bookmark,
                        onEditNote = { onOpenNoteDialog(bookmark) },
                        onDelete = { onDeleteBookmark(bookmark.recordKey) },
                        onCopy = {
                            val text = "${bookmark.location}\nSets: ${bookmark.setNumbers}\nANI: ${bookmark.aniNumbers}"
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Copied details", Toast.LENGTH_SHORT).show()
                        },
                        onShare = {
                            val text = "${bookmark.location}\nS.L. ${bookmark.slNo} (p.${bookmark.pdfPage})\nSets: ${bookmark.setNumbers}\nANI: ${bookmark.aniNumbers}\nNote: ${bookmark.note}"
                            shareText(context, text, "Saved Record: ${bookmark.location}")
                        }
                    )
                }
            }
        }
    }

    // Edit Note Dialog
    noteDialogItem?.let { item ->
        var noteText by remember(item.recordKey) { mutableStateOf(item.note) }

        AlertDialog(
            onDismissRequest = onCloseNoteDialog,
            title = {
                Text(
                    text = "Duty Note for ${item.location}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "इस वायरलेस सेट/थाने के संबंध में विवरण या ड्यूटी नोट दर्ज करें:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_input"),
                        placeholder = { Text("उदाहरण: PRV 4 में कार्यरत, एंटीना ठीक है...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onSaveNote(item.recordKey, noteText) },
                    colors = ButtonDefaults.buttonColors(containerColor = PoliceNavy800)
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseNoteDialog) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BookmarkItemCard(
    bookmark: BookmarkEntity,
    onEditNote: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📍 ${bookmark.location}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "S.L. No. ${bookmark.slNo} • PDF Page ${bookmark.pdfPage}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RadioRed.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (bookmark.setNumbers.isNotBlank()) {
                Row {
                    Text(
                        text = "Set Codes: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PoliceNavy800
                    )
                    Text(
                        text = bookmark.setNumbers,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (bookmark.aniNumbers.isNotBlank()) {
                Row {
                    Text(
                        text = "ANI Codes: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PoliceBlue700
                    )
                    Text(
                        text = bookmark.aniNumbers,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Note Box
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PoliceBlue100)
                    .clickable { onEditNote() }
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = PoliceBlue700,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (bookmark.note.isBlank()) "Add personal note/duty remarks..." else bookmark.note,
                            fontSize = 12.sp,
                            color = if (bookmark.note.isBlank()) Color.Gray else PoliceNavy900,
                            fontWeight = if (bookmark.note.isBlank()) FontWeight.Normal else FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Note",
                        tint = PoliceBlue700,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = PoliceBlue700,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PoliceBlue700,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
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
