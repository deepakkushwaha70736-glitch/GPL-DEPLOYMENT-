package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PoliceBlue100
import com.example.ui.theme.PoliceBlue700
import com.example.ui.theme.PoliceGold
import com.example.ui.theme.PoliceNavy800
import com.example.ui.theme.PoliceNavy900
import com.example.ui.theme.RadioGreen

@Composable
fun AboutUsScreen(
    onNavigateToDirectory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_us_screen")
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PoliceNavy900, PoliceNavy800, PoliceBlue700)
                    )
                )
                .statusBarsPadding()
                .padding(18.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(PoliceGold.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalPolice,
                            contentDescription = null,
                            tint = PoliceGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Hathras District Police",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "उत्तर प्रदेश पुलिस • हाथरस जनपद",
                            fontSize = 12.sp,
                            color = PoliceBlue100.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "ध्येय वाक्य (Motto): \"सुरक्षा आपकी, संकल्प हमारा\"",
                        color = PoliceGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mission Card
            item {
                AboutSectionCard(
                    title = "OUR MISSION",
                    hindiTitle = "हमारा मिशन",
                    icon = Icons.Default.Security,
                    iconTint = PoliceGold
                ) {
                    Text(
                        text = "To preserve the peace, prevent crime, protect constitutional rights, and ensure swift emergency response across Hathras district. Through high-frequency wireless communications, transparent public grievance mechanisms, and modern digital policing, we strive to build a safe and crime-free environment for all citizens.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "हाथरस पुलिस का मुख्य उद्देश्य जनमानस की सुरक्षा, कानून व्यवस्था का सुदृढ़ संधारण, अपराध पर अंकुश लगाना तथा वायरलेस दूरसंचार प्रणाली के माध्यम से 24x7 तत्काल सहायता सुनिश्चित करना है।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            // Core Values Card
            item {
                AboutSectionCard(
                    title = "CORE VALUES & PRINCIPLES",
                    hindiTitle = "मूल मूल्य एवं सिद्धांत",
                    icon = Icons.Default.Shield,
                    iconTint = PoliceBlue700
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ValueItem(
                            icon = Icons.Default.Shield,
                            title = "Integrity & Impartiality (सत्यनिष्ठा एवं निष्पक्षता)",
                            desc = "Uncompromising adherence to rule of law without bias, fear, or favor."
                        )
                        ValueItem(
                            icon = Icons.Default.Speed,
                            title = "Tactical Wireless Speed (त्वरित प्रतिक्रिया)",
                            desc = "Average Dial 112 and PRV wireless response time under 8 minutes across urban and rural zones."
                        )
                        ValueItem(
                            icon = Icons.Default.Handshake,
                            title = "Community Trust (जन-मित्र पुलिसिंग)",
                            desc = "Compassionate policing focused on women safety, elder protection, and transparent community interaction."
                        )
                        ValueItem(
                            icon = Icons.Default.CellTower,
                            title = "Technological Excellence (तकनीकी दक्षता)",
                            desc = "Advanced Kenwood NX3720/NX3220 digital wireless telemetry with ANI caller identification."
                        )
                    }
                }
            }

            // History Card
            item {
                AboutSectionCard(
                    title = "HISTORY & TELECOM HERITAGE",
                    hindiTitle = "इतिहास एवं दूरसंचार परंपरा",
                    icon = Icons.Default.History,
                    iconTint = Color(0xFFD97706)
                ) {
                    Text(
                        text = "Hathras is an ancient cultural and commercial hub in the historic Braj territory of Western Uttar Pradesh. Initially known as Mahamaya Nagar when formed in 1997, it was formally designated as Hathras District, comprising major tehsils of Hathras, Sadabad, and Sikandra Rao.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The General Police Line (GPL) Telecommunication Branch in Hathras manages the vital nervous system of district security. Over the years, the wireless grid evolved from analog single-channel transceivers to an integrated Kenwood digital system featuring 120+ specialized fixed stations, mobile police cruisers (PRVs), and handheld walkie-talkies with dedicated repeaters and telescopic pneumatic masts.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                }
            }

            // Key Leadership Card
            item {
                AboutSectionCard(
                    title = "KEY DISTRICT LEADERSHIP",
                    hindiTitle = "जनपद पुलिस नेतृत्व",
                    icon = Icons.Default.LocalPolice,
                    iconTint = PoliceNavy800
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LeaderItem(
                            rank = "SUPERINTENDENT OF POLICE (SP)",
                            hindiRank = "पुलिस अधीक्षक, हाथरस",
                            scope = "Overall in-charge of law enforcement, security, and administrative governance across Hathras district.",
                            cugNumber = "9454400275"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LeaderItem(
                            rank = "ADDITIONAL SP (ASP)",
                            hindiRank = "अपर पुलिस अधीक्षक, हाथरस",
                            scope = "Crime branch supervision, departmental operations, and public grievance monitoring.",
                            cugNumber = "9454401062"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LeaderItem(
                            rank = "CIRCLE OFFICER (CO) CITY",
                            hindiRank = "क्षेत्राधिकारी नगर (हाथरस)",
                            scope = "Superintending Kotwali Hathras, Thana Hathras Gate, and urban municipal beats.",
                            cugNumber = "9454401344"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LeaderItem(
                            rank = "CIRCLE OFFICER (CO) SADABAD",
                            hindiRank = "क्षेत्राधिकारी सादाबाद",
                            scope = "Superintending Thana Sadabad, Thana Sahpau, and Thana Chandpa.",
                            cugNumber = "9454401345"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LeaderItem(
                            rank = "CIRCLE OFFICER (CO) SIKANDRA RAO",
                            hindiRank = "क्षेत्राधिकारी सिकंदराराऊ",
                            scope = "Superintending Thana Sikandra Rao, Thana Hasayan, and rural outposts.",
                            cugNumber = "9454401346"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LeaderItem(
                            rank = "DISTRICT TELECOM IN-CHARGE",
                            hindiRank = "प्रभारी दूरसंचार एवं वायरलैस नियंत्रण कक्ष",
                            scope = "Overseeing radio frequency spectrum, GPL equipment deployments, repeater maintenance, and NX3720/NX3220 maintenance.",
                            cugNumber = "9454403756"
                        )
                    }
                }
            }

            // Quick Contact & Directory CTA
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PoliceNavy900),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "24x7 POLICE CONTROL ROOM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PoliceGold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Need emergency police assistance or wireless dispatch?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Dial 112 for immediate Police/Fire/Medical emergency dispatch or contact the Hathras Police Control Room at 05662-232100.",
                            fontSize = 12.sp,
                            color = PoliceBlue100.copy(alpha = 0.85f),
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:112")
                                    }
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call 112", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onNavigateToDirectory,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PoliceBlue700)
                            ) {
                                Text("View Directory", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Developer Credit
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Developed by AO DEEPAK KUSHWAHA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        letterSpacing = 0.4.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Hathras Police Telecommunication Division • Official GPL Roster",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutSectionCard(
    title: String,
    hindiTitle: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = PoliceNavy900,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = hindiTitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            content()
        }
    }
}

@Composable
private fun ValueItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PoliceBlue700,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun LeaderItem(
    rank: String,
    hindiRank: String,
    scope: String,
    cugNumber: String
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rank,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = PoliceNavy900
            )
            Text(
                text = hindiRank,
                fontSize = 11.sp,
                color = PoliceBlue700,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = scope,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$cugNumber")
                }
                context.startActivity(intent)
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = PoliceBlue100),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = PoliceNavy900,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Call",
                fontSize = 11.sp,
                color = PoliceNavy900,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
