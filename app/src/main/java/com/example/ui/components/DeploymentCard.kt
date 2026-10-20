package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GplDeployment
import com.example.ui.theme.HighlightYellow
import com.example.ui.theme.PoliceBlue100
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.RadioAmber
import com.example.ui.theme.RadioAmberBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeploymentCard(
    deployment: GplDeployment,
    searchQuery: String,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopyCode: (String) -> Unit,
    onCopySummary: () -> Unit,
    onShare: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deployment_card_${deployment.slNo}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Location and S.L. No
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (deployment.location.isNullOrBlank()) {
                            AnnotatedString("स्थान नाम PDF में उपलब्ध नहीं")
                        } else {
                            highlightQuery(deployment.location, searchQuery)
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "S.L. No. ${deployment.slNo} • PDF Page ${deployment.pdfPage}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    if (!deployment.distanceFromDcr.isNullOrBlank() || !deployment.antennaHeightFeet.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            deployment.distanceFromDcr?.let { dist ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PoliceNavy900)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "📍 DCR से $dist",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PoliceGold
                                    )
                                }
                            }
                            deployment.antennaHeightFeet?.let { height ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PoliceBlue100)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "🗼 एंटीना: $height",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PoliceNavy900
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_btn_${deployment.slNo}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) PoliceGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SET / EQUIPMENT CODES Section
            SectionHeader(title = "SET / EQUIPMENT CODES")
            if (deployment.equipmentCodes.isEmpty()) {
                Text(
                    text = "PDF में उपलब्ध नहीं",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    deployment.equipmentCodes.forEach { code ->
                        EquipmentChip(
                            code = code,
                            searchQuery = searchQuery,
                            onCopy = { onCopyCode(code) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ANI NUMBER Section
            SectionHeader(title = "ANI NUMBER (CALLER ID)")
            if (deployment.aniRecords.isEmpty()) {
                Text(
                    text = "ANI PDF में उपलब्ध नहीं",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    deployment.aniRecords.forEach { record ->
                        val aniText = record.ani ?: "—"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PoliceBlue100)
                                .clickable { onCopyCode(aniText) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "📞 ",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = highlightQuery(aniText, searchQuery),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PoliceNavy900
                                )
                                if (!record.source.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = record.source,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PoliceNavy800.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ANTENNA Section (2 columns)
            SectionHeader(title = "ANTENNA DEPLOYMENT")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AntennaBox(
                    label = "0 db GP",
                    count = deployment.antenna.zeroDbGp,
                    modifier = Modifier.weight(1f)
                )
                AntennaBox(
                    label = "3 db GP",
                    count = deployment.antenna.threeDbGp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AntennaBox(
                    label = "0 db Magnetic",
                    count = deployment.antenna.zeroDbMagnetic,
                    modifier = Modifier.weight(1f)
                )
                AntennaBox(
                    label = "Whip Antenna",
                    count = deployment.antenna.whipAntenna,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // EQUIPMENT DETAILS Table
            SectionHeader(title = "EQUIPMENT DETAILS")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                DetailRow("ST/MOB SET NX3720", deployment.stMobSetNX3720, searchQuery)
                DetailRow("POWER SUPPLY", formatUnit(deployment.powerSupply), searchQuery)
                DetailRow("12V BATTERY", formatBattery(deployment.battery12V), searchQuery)
                DetailRow("T. MAST", formatUnit(deployment.tMast), searchQuery)
                DetailRow("GI POLE", formatUnit(deployment.giPole), searchQuery)
                DetailRow("PNEUMATIC MAST", formatUnit(deployment.pneumaticMast), searchQuery)
                DetailRow("H/H SET NX3220", deployment.hhSetNX3220, searchQuery)
                DetailRow("S/U BATTS / CHARGER", formatUnit(deployment.suBattsCharger), searchQuery)
                DetailRow("PA SYSTEM", formatUnit(deployment.paSystem), searchQuery)
                if (!deployment.distanceFromDcr.isNullOrBlank()) {
                    DetailRow("DISTANCE FROM DCR", deployment.distanceFromDcr, searchQuery)
                }
                if (!deployment.antennaHeightFeet.isNullOrBlank()) {
                    DetailRow("ANTENNA HEIGHT", deployment.antennaHeightFeet, searchQuery)
                }
                if (!deployment.remarks.isNullOrBlank()) {
                    DetailRow("REMARKS", deployment.remarks, searchQuery)
                }
            }

            if (deployment.aniOnly) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RadioAmberBg)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "ℹ️ यह entry ANI PDF में है; GPL deployment PDF में इसका location/equipment deployment record नहीं मिला।",
                        fontSize = 11.sp,
                        color = RadioAmber,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCopySummary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy record",
                            modifier = Modifier.size(18.dp),
                            tint = PoliceBlue700
                        )
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(18.dp),
                            tint = PoliceBlue700
                        )
                    }
                }

                OutlinedButton(
                    onClick = onViewDetail,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Full Details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp,
        color = PoliceNavy800.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun EquipmentChip(
    code: String,
    searchQuery: String,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PoliceNavy800.copy(alpha = 0.08f))
            .border(1.dp, PoliceBlue700.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onCopy() }
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = highlightQuery(code, searchQuery),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PoliceNavy900
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy code",
                modifier = Modifier.size(12.dp),
                tint = PoliceBlue700
            )
        }
    }
}

@Composable
private fun AntennaBox(
    label: String,
    count: String?,
    modifier: Modifier = Modifier
) {
    val hasCount = !count.isNullOrBlank()
    val cleanCount = if (hasCount) {
        val trimmed = count!!.trim()
        if (trimmed == "01") "1" else trimmed
    } else null

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (hasCount) PoliceBlue100.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                width = if (hasCount) 1.dp else 0.5.dp,
                color = if (hasCount) PoliceBlue700.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 9.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📡 $label",
                fontSize = 11.sp,
                fontWeight = if (hasCount) FontWeight.Bold else FontWeight.Medium,
                color = if (hasCount) PoliceNavy900 else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (hasCount) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(PoliceNavy900)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (cleanCount?.all { it.isDigit() } == true) "$cleanCount unit" else (cleanCount ?: "1"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = PoliceGold
                    )
                }
            } else {
                Text(
                    text = "—",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String?,
    searchQuery: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = if (value.isNullOrBlank()) {
                AnnotatedString("—")
            } else {
                highlightQuery(value, searchQuery)
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (!value.isNullOrBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f)
        )
    }
}

fun highlightQuery(text: String, query: String): AnnotatedString {
    val q = query.trim()
    if (q.isBlank()) return AnnotatedString(text)

    val lowerText = text.lowercase()
    val lowerQuery = q.lowercase()

    return buildAnnotatedString {
        var startIndex = 0
        while (startIndex < text.length) {
            val matchIndex = lowerText.indexOf(lowerQuery, startIndex)
            if (matchIndex == -1) {
                append(text.substring(startIndex))
                break
            }

            append(text.substring(startIndex, matchIndex))
            val endIndex = matchIndex + lowerQuery.length
            pushStyle(SpanStyle(background = HighlightYellow, fontWeight = FontWeight.Black, color = Color.Black))
            append(text.substring(matchIndex, endIndex))
            pop()

            startIndex = endIndex
        }
    }
}

private fun formatUnit(v: String?): String? {
    if (v.isNullOrBlank()) return null
    return if (v.all { it.isDigit() }) {
        val num = v.toIntOrNull() ?: 1
        "$v ${if (num == 1) "unit" else "units"}"
    } else v
}

private fun formatBattery(v: String?): String? {
    if (v.isNullOrBlank()) return null
    return if (v.all { it.isDigit() }) {
        val num = v.toIntOrNull() ?: 1
        "$v ${if (num == 1) "battery" else "batteries"}"
    } else v
}
