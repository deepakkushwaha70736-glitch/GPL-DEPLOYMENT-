package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RadioCodeItem
import com.example.ui.theme.PoliceBlue100
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.RadioAmber
import com.example.ui.theme.RadioGreen

@Composable
fun RadioGuideScreen(
    radioGuideItems: List<RadioCodeItem>,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("Hardware & Sets", "Ten-Codes", "Radio Etiquette", "Phonetics")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("radio_guide_screen")
    ) {
        // Guide Header
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
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = PoliceGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "📖 Radio Manual & Protocols",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Kenwood NX3720 / NX3220 Specs, Codes & Discipline",
                        fontSize = 12.sp,
                        color = PoliceBlue100.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Section Tabs
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = PoliceBlue700
        ) {
            sections.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSection == index,
                    onClick = { selectedSection = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (selectedSection) {
                0 -> {
                    // Hardware & Sets
                    item {
                        HardwareCard(
                            title = "KENWOOD NX-3720",
                            subtitle = "VHF/UHF Digital & FM Analog Mobile / Base Transceiver",
                            specs = listOf(
                                "512 Channels / 128 Zones capability",
                                "NXDN Digital Conventional & Trunking dual mode",
                                "Built-in GPS & Automatic Number Identification (ANI)",
                                "Output Power: 5W / 25W / 45W / 50W RF Output",
                                "Front panel Emergency key & Voice Announcement",
                                "MIL-STD 810 C/D/E/F/G & IP54 water/dust resistant"
                            )
                        )
                    }

                    item {
                        HardwareCard(
                            title = "KENWOOD NX-3220",
                            subtitle = "VHF/UHF Multi-Protocol Handheld Walkie-Talkie Set",
                            specs = listOf(
                                "Compact & rugged handheld for field patrol officers",
                                "Full keypad with 4-line Dot Matrix LCD Display",
                                "Active Noise Reduction & 1000mW loud speaker audio",
                                "7-color Light Bar LED Indicator for duty status",
                                "Li-ion Battery Pack (KNB-57L / KNB-55L)",
                                "IP67 / IP68 Submersible water immersion rating"
                            )
                        )
                    }

                    item {
                        HardwareCard(
                            title = "ANTENNA & MAST DEPLOYMENT",
                            subtitle = "Ground Plane, Vehicle Mounts & Pneumatic Towers",
                            specs = listOf(
                                "0 dB GP Antenna: Omni-directional 360° station antenna",
                                "3 dB GP Antenna: High-gain extended distance repeater/base",
                                "0 dB Magnetic Antenna: PRV and escort car rooftop mount",
                                "Whip Antenna: Flexible helical antenna on walkie-talkie",
                                "T. Mast & GI Pole: Galvanized iron tubular base antenna masts",
                                "Pneumatic Mast: Telescoping compressed air elevated mast"
                            )
                        )
                    }
                }

                1 -> {
                    // Ten-Codes
                    val codes = radioGuideItems.filter { it.category == "Ten-Code" }
                    items(codes) { codeItem ->
                        RadioCodeCard(codeItem)
                    }
                }

                2 -> {
                    // Radio Etiquette & Procedures
                    val procedures = radioGuideItems.filter { it.category == "Procedure" || it.category == "Protocol" }
                    items(procedures) { procItem ->
                        RadioCodeCard(procItem)
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ Radio Wireless Golden Rules",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PoliceNavy900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "1. PTT (Push-To-Talk) दबाने से पहले 1 सेकंड रुकें ताकि ANI ट्रांसमिट हो सके।\n" +
                                            "2. संदेश हमेशा संक्षिप्त, सटीक और शांत स्वर में बोलें।\n" +
                                            "3. बात पूरी होने पर 'OVER' कहें। यदि उत्तर की आवश्यकता न हो तो 'OUT' कहें।\n" +
                                            "4. महत्वपूर्ण कोड या नाम हमेशा फोनेटिक अल्फाबेट में स्पष्ट करें।",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // Phonetic Alphabet
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🔤 International Police Phonetic Alphabet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PoliceNavy900
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "A - Alfa        B - Bravo       C - Charlie\n" +
                                            "D - Delta       E - Echo        F - Foxtrot\n" +
                                            "G - Golf        H - Hotel       I - India\n" +
                                            "J - Juliett     K - Kilo        L - Lima\n" +
                                            "M - Mike        N - November    O - Oscar\n" +
                                            "P - Papa        Q - Quebec      R - Romeo\n" +
                                            "S - Sierra      T - Tango       U - Uniform\n" +
                                            "V - Victor      W - Whiskey     X - X-ray\n" +
                                            "Y - Yankee      Z - Zulu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HardwareCard(
    title: String,
    subtitle: String,
    specs: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PoliceNavy800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CellTower,
                        contentDescription = null,
                        tint = PoliceGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PoliceNavy900
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            specs.forEach { spec ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "• ", color = PoliceBlue700, fontWeight = FontWeight.Bold)
                    Text(
                        text = spec,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RadioCodeCard(item: RadioCodeItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.code,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PoliceNavy900
                )
                Text(
                    text = item.meaning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PoliceBlue700
                )
                Text(
                    text = item.hindiMeaning,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PoliceNavy800.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PoliceNavy800
                )
            }
        }
    }
}
