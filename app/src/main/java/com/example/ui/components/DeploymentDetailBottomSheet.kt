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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GplDeployment
import com.example.ui.theme.PoliceBlue100
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DeploymentDetailBottomSheet(
    deployment: GplDeployment,
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyCode: (String) -> Unit,
    onShare: () -> Unit,
    onDialOrCopyAni: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("deployment_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PoliceBlue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = PoliceBlue700,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Deployment Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hathras Police Telecommunication Record",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PoliceNavy900)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "📍 LOCATION / THANA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = PoliceGold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deployment.location ?: "स्थान नाम PDF में उपलब्ध नहीं (ANI Record)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Serial No: ${deployment.slNo}   •   PDF Source Page: ${deployment.pdfPage}",
                        fontSize = 12.sp,
                        color = PoliceBlue100.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Equipment Codes Section
            Text(
                text = "SET & EQUIPMENT IDENTIFIERS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PoliceNavy800
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (deployment.equipmentCodes.isEmpty()) {
                Text(text = "No equipment codes listed in PDF", fontSize = 13.sp, color = Color.Gray)
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    deployment.equipmentCodes.forEach { code ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PoliceBlue100)
                                .border(1.dp, PoliceBlue700.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable { onCopyCode(code) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = code,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PoliceNavy900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = PoliceBlue700,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ANI Records Section
            Text(
                text = "ANI NUMBERS (WIRELESS CALLER IDS)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PoliceNavy800
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (deployment.aniRecords.isEmpty()) {
                Text(text = "No ANI recorded in PDF", fontSize = 13.sp, color = Color.Gray)
            } else {
                deployment.aniRecords.forEach { record ->
                    val ani = record.ani ?: "—"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "📞 ANI: $ani",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PoliceNavy900
                                    )
                                    if (record.source != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${record.source})",
                                            fontSize = 11.sp,
                                            color = PoliceBlue700,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                if (record.setNumber != null) {
                                    Text(
                                        text = "Mapped Set Code: ${record.setNumber}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row {
                                IconButton(onClick = { onCopyCode(ani) }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy ANI",
                                        tint = PoliceBlue700,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Antenna & Hardware Breakdown
            Text(
                text = "ANTENNA & INFRASTRUCTURE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PoliceNavy800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                SpecRow("0 db GP Base Antenna", deployment.antenna.zeroDbGp)
                SpecRow("3 db GP Base Antenna", deployment.antenna.threeDbGp)
                SpecRow("0 db Magnetic Vehicle Antenna", deployment.antenna.zeroDbMagnetic)
                SpecRow("Whip Antenna (Handheld)", deployment.antenna.whipAntenna)
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                SpecRow("Kenwood NX3720 Base/Mobile", deployment.stMobSetNX3720)
                SpecRow("Kenwood NX3220 Handheld", deployment.hhSetNX3220)
                SpecRow("Power Supply Unit", deployment.powerSupply)
                SpecRow("12V Battery Backup", deployment.battery12V)
                SpecRow("T. Mast", deployment.tMast)
                SpecRow("GI Pole", deployment.giPole)
                SpecRow("Pneumatic Mast", deployment.pneumaticMast)
                SpecRow("Spare Battery / Charger", deployment.suBattsCharger)
                SpecRow("PA (Public Address) System", deployment.paSystem)
                if (!deployment.remarks.isNullOrBlank()) {
                    SpecRow("Officer Remarks", deployment.remarks)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onToggleBookmark,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBookmarked) PoliceGold else PoliceNavy800
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBookmarked) "Saved" else "Bookmark",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share Record", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SpecRow(title: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value ?: "—",
            fontSize = 12.sp,
            color = if (value != null) PoliceNavy900 else Color.Gray,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
