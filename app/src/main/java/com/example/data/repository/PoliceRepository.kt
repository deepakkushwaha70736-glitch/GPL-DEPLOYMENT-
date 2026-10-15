package com.example.data.repository

import android.content.Context
import com.example.data.local.BookmarkEntity
import com.example.data.local.PoliceDao
import com.example.data.local.RecentSearchEntity
import com.example.data.model.AniRecord
import com.example.data.model.AntennaInfo
import com.example.data.model.DirectoryContact
import com.example.data.model.FilterCategory
import com.example.data.model.GplDeployment
import com.example.data.model.RadioCodeItem
import com.example.data.model.SearchMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class PoliceRepository(
    private val context: Context,
    private val dao: PoliceDao
) {
    private var isInitialized = false
    private val gplDeployments = mutableListOf<GplDeployment>()
    private val allAni = mutableListOf<AniRecord>()
    private val gplByCode = mutableMapOf<String, MutableList<GplDeployment>>()
    private val allUniqueCodes = mutableSetOf<String>()

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val recentSearches: Flow<List<RecentSearchEntity>> = dao.getRecentSearches()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        try {
            // Load GPL Deployments
            val gplJsonStr = context.assets.open("gpl_deployments.json").bufferedReader().use { it.readText() }
            val gplArray = JSONArray(gplJsonStr)

            for (i in 0 until gplArray.length()) {
                val obj = gplArray.getJSONObject(i)
                val slNo = obj.optInt("slNo")
                val pdfPage = obj.optString("pdfPage", "")
                val location = obj.optString("location", "").takeIf { it != "null" && it.isNotBlank() }
                val stMobSet = obj.optString("stMobSetNX3720", "").takeIf { it != "null" && it.isNotBlank() }
                
                val antennaObj = obj.optJSONObject("antenna")
                val antenna = AntennaInfo(
                    zeroDbGp = antennaObj?.optString("0 db GP", "")?.takeIf { it != "null" && it.isNotBlank() },
                    threeDbGp = antennaObj?.optString("3 db GP", "")?.takeIf { it != "null" && it.isNotBlank() },
                    zeroDbMagnetic = antennaObj?.optString("0 db Magnetic", "")?.takeIf { it != "null" && it.isNotBlank() },
                    whipAntenna = antennaObj?.optString("Whip Antenna", "")?.takeIf { it != "null" && it.isNotBlank() }
                )

                val powerSupply = obj.optString("powerSupply", "").takeIf { it != "null" && it.isNotBlank() }
                val battery12V = obj.optString("battery12V", "").takeIf { it != "null" && it.isNotBlank() }
                val tMast = obj.optString("tMast", "").takeIf { it != "null" && it.isNotBlank() }
                val giPole = obj.optString("giPole", "").takeIf { it != "null" && it.isNotBlank() }
                val pneumaticMast = obj.optString("pneumaticMast", "").takeIf { it != "null" && it.isNotBlank() }
                val hhSet = obj.optString("hhSetNX3220", "").takeIf { it != "null" && it.isNotBlank() }
                val suBattsCharger = obj.optString("suBattsCharger", "").takeIf { it != "null" && it.isNotBlank() }
                val paSystem = obj.optString("paSystem", "").takeIf { it != "null" && it.isNotBlank() }
                val remarks = obj.optString("remarks", "").takeIf { it != "null" && it.isNotBlank() }

                val eqCodesList = mutableListOf<String>()
                val eqCodesArray = obj.optJSONArray("equipmentCodes")
                if (eqCodesArray != null) {
                    for (c in 0 until eqCodesArray.length()) {
                        val code = eqCodesArray.getString(c)
                        if (code.isNotBlank() && code != "null") {
                            eqCodesList.add(code.trim())
                            allUniqueCodes.add(norm(code))
                        }
                    }
                }

                val aniList = mutableListOf<AniRecord>()
                val aniArray = obj.optJSONArray("aniRecords")
                if (aniArray != null) {
                    for (a in 0 until aniArray.length()) {
                        val aniObj = aniArray.getJSONObject(a)
                        aniList.add(
                            AniRecord(
                                slNo = aniObj.optInt("slNo"),
                                setNumber = aniObj.optString("setNumber", "").takeIf { it != "null" && it.isNotBlank() },
                                ani = aniObj.optString("ani", "").takeIf { it != "null" && it.isNotBlank() },
                                whereDeployed = aniObj.optString("whereDeployed", "").takeIf { it != "null" && it.isNotBlank() },
                                pdfPage = aniObj.optInt("pdfPage", 0).takeIf { it > 0 },
                                source = aniObj.optString("source", "").takeIf { it != "null" && it.isNotBlank() }
                            )
                        )
                    }
                }

                val deployment = GplDeployment(
                    slNo = slNo,
                    pdfPage = pdfPage,
                    location = location,
                    stMobSetNX3720 = stMobSet,
                    antenna = antenna,
                    powerSupply = powerSupply,
                    battery12V = battery12V,
                    tMast = tMast,
                    giPole = giPole,
                    pneumaticMast = pneumaticMast,
                    hhSetNX3220 = hhSet,
                    suBattsCharger = suBattsCharger,
                    paSystem = paSystem,
                    remarks = remarks,
                    equipmentCodes = eqCodesList,
                    aniRecords = aniList,
                    aniOnly = false
                )
                gplDeployments.add(deployment)

                for (code in eqCodesList) {
                    val nCode = norm(code)
                    gplByCode.getOrPut(nCode) { mutableListOf() }.add(deployment)
                }
            }

            // Load All ANI records
            val aniJsonStr = context.assets.open("all_ani.json").bufferedReader().use { it.readText() }
            val allAniArray = JSONArray(aniJsonStr)
            for (i in 0 until allAniArray.length()) {
                val aniObj = allAniArray.getJSONObject(i)
                val setNum = aniObj.optString("setNumber", "").takeIf { it != "null" && it.isNotBlank() }
                if (!setNum.isNullOrBlank()) {
                    allUniqueCodes.add(norm(setNum))
                }
                allAni.add(
                    AniRecord(
                        slNo = aniObj.optInt("slNo"),
                        setNumber = setNum,
                        ani = aniObj.optString("ani", "").takeIf { it != "null" && it.isNotBlank() },
                        whereDeployed = aniObj.optString("whereDeployed", "").takeIf { it != "null" && it.isNotBlank() },
                        pdfPage = aniObj.optInt("pdfPage", 0).takeIf { it > 0 },
                        source = aniObj.optString("source", "").takeIf { it != "null" && it.isNotBlank() }
                    )
                )
            }

            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTotalEntriesCount(): Int = gplDeployments.size
    fun getTotalUniqueCodesCount(): Int = allUniqueCodes.size

    fun searchDeployments(query: String, mode: SearchMode, category: FilterCategory): List<GplDeployment> {
        val q = norm(query)

        val rawMatches = if (q.isBlank()) {
            if (category == FilterCategory.ALL) {
                // If query is blank and category is ALL, show top default/featured deployments
                gplDeployments.take(30)
            } else {
                gplDeployments
            }
        } else {
            val primary = gplDeployments.filter { row ->
                when (mode) {
                    SearchMode.SET -> row.equipmentCodes.any { norm(it).contains(q) }
                    SearchMode.ANI -> row.aniRecords.any { norm(it.ani ?: "").contains(q) }
                    SearchMode.PLACE -> norm(row.location ?: "").contains(q)
                    SearchMode.ALL -> {
                        row.equipmentCodes.any { norm(it).contains(q) } ||
                                row.aniRecords.any { norm(it.ani ?: "").contains(q) } ||
                                norm(row.location ?: "").contains(q) ||
                                norm(row.stMobSetNX3720 ?: "").contains(q) ||
                                norm(row.hhSetNX3220 ?: "").contains(q) ||
                                norm(row.remarks ?: "").contains(q)
                    }
                }
            }

            val aniOnly = if (mode == SearchMode.ANI || mode == SearchMode.ALL) {
                val linkedKeys = primary.map { "${it.slNo}|${it.pdfPage}" }.toSet()
                val list = mutableListOf<GplDeployment>()
                for (a in allAni) {
                    val aniMatch = norm(a.ani ?: "").contains(q)
                    val setMatch = norm(a.setNumber ?: "").contains(q)
                    if (!aniMatch && !setMatch) continue

                    val hasLinkedGpl = a.setNumber?.let { gplByCode.containsKey(norm(it)) } ?: false
                    if (hasLinkedGpl) continue

                    list.add(
                        GplDeployment(
                            slNo = a.slNo,
                            pdfPage = "ANI PDF p.${a.pdfPage ?: 1}",
                            location = null,
                            stMobSetNX3720 = null,
                            antenna = AntennaInfo(),
                            powerSupply = null,
                            battery12V = null,
                            tMast = null,
                            giPole = null,
                            pneumaticMast = null,
                            hhSetNX3220 = null,
                            suBattsCharger = null,
                            paSystem = null,
                            remarks = null,
                            equipmentCodes = listOfNotNull(a.setNumber),
                            aniRecords = listOf(a),
                            aniOnly = true
                        )
                    )
                }
                list.filterNot { linkedKeys.contains("${it.slNo}|${it.pdfPage}") }
            } else {
                emptyList()
            }

            primary + aniOnly
        }

        // Apply Category Filters
        return rawMatches.filter { item ->
            when (category) {
                FilterCategory.ALL -> true
                FilterCategory.THANA -> {
                    val loc = (item.location ?: "").uppercase()
                    loc.contains("THANA") || loc.contains("KOTWALI") || loc.contains("SO ") ||
                            loc.contains("CHOWKI") || loc.contains("SASNI") || loc.contains("SADABAD") ||
                            loc.contains("SIKANDRA") || loc.contains("CHANDPA") || loc.contains("HASAYAN") ||
                            loc.contains("SAHPAU") || loc.contains("MURSAN") || loc.contains("HATHRAS GATE")
                }
                FilterCategory.OFFICERS -> {
                    val loc = (item.location ?: "").uppercase()
                    loc.contains("DM") || loc.contains("SP") || loc.contains("ASP") ||
                            loc.contains("CO ") || loc.contains("RESI") || loc.contains("OFFICE") ||
                            loc.contains("CHAMBER")
                }
                FilterCategory.MOBILES -> {
                    val loc = (item.location ?: "").uppercase()
                    loc.contains("MOB") || loc.contains("BOLERO") || loc.contains("INNOVA") ||
                            loc.contains("ESCORT") || loc.contains("VEHICLE") || loc.contains("PRV")
                }
                FilterCategory.BASE_MAST -> {
                    !item.tMast.isNullOrBlank() || !item.giPole.isNullOrBlank() || !item.pneumaticMast.isNullOrBlank()
                }
                FilterCategory.HANDHELD -> {
                    !item.hhSetNX3220.isNullOrBlank() || item.equipmentCodes.any { it.startsWith("C32") }
                }
                FilterCategory.BASE_SET -> {
                    !item.stMobSetNX3720.isNullOrBlank() || item.equipmentCodes.any { it.startsWith("C41") || it.startsWith("C42") }
                }
                FilterCategory.PA_SYSTEM -> {
                    !item.paSystem.isNullOrBlank()
                }
            }
        }
    }

    suspend fun saveRecentSearch(query: String, mode: SearchMode) = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext
        dao.deleteRecentSearch(query.trim(), mode.name)
        dao.insertSearch(RecentSearchEntity(query = query.trim(), mode = mode.name))
    }

    suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        dao.clearRecentSearches()
    }

    suspend fun toggleBookmark(deployment: GplDeployment) = withContext(Dispatchers.IO) {
        val key = deployment.uniqueKey
        if (dao.isBookmarked(key)) {
            dao.deleteBookmark(key)
        } else {
            dao.insertBookmark(
                BookmarkEntity(
                    recordKey = key,
                    location = deployment.location ?: "ANI Record",
                    slNo = deployment.slNo,
                    pdfPage = deployment.pdfPage,
                    setNumbers = deployment.equipmentCodes.joinToString(", "),
                    aniNumbers = deployment.aniRecords.mapNotNull { it.ani }.joinToString(", "),
                    note = ""
                )
            )
        }
    }

    suspend fun updateBookmarkNote(key: String, note: String) = withContext(Dispatchers.IO) {
        dao.updateNote(key, note)
    }

    suspend fun removeBookmark(key: String) = withContext(Dispatchers.IO) {
        dao.deleteBookmark(key)
    }

    fun getDirectoryContacts(): List<DirectoryContact> {
        return listOf(
            DirectoryContact("Emergency Police", "Uttar Pradesh Emergency Control Room", "112", "Emergency Helplines", true, "🚨"),
            DirectoryContact("Women Helpline", "UP Police Women Powerline", "1090", "Emergency Helplines", true, "🛡️"),
            DirectoryContact("Cyber Crime", "National Cyber Crime Reporting Portal", "1930", "Emergency Helplines", true, "💻"),
            DirectoryContact("Child Helpline", "Child Protection Services", "1098", "Emergency Helplines", true, "🧒"),
            DirectoryContact("Fire Service", "Uttar Pradesh Fire Service", "101", "Emergency Helplines", true, "🚒"),
            DirectoryContact("Ambulance", "UP Medical Emergency Service", "108", "Emergency Helplines", true, "🚑"),
            
            DirectoryContact("Hathras Police Control Room", "District Telecommunication & Wireless Dispatch", "05662-232100", "Control Room", false, "📻"),
            DirectoryContact("Control Room In-charge", "Wireless Communication In-charge Hathras", "9454403756", "Control Room", false, "📡"),
            DirectoryContact("SP Hathras (CUG)", "Superintendent of Police Hathras", "9454400275", "Senior Officers", false, "⭐"),
            DirectoryContact("ASP Hathras (CUG)", "Additional Superintendent of Police", "9454401062", "Senior Officers", false, "⭐"),
            DirectoryContact("CO City Hathras", "Circle Officer City (Kotwali / Hathras Gate)", "9454401344", "Senior Officers", false, "👮"),
            DirectoryContact("CO Sadabad", "Circle Officer Sadabad / Sahpau / Chandpa", "9454401345", "Senior Officers", false, "👮"),
            DirectoryContact("CO Sikandra Rao", "Circle Officer Sikandra Rao / Hasayan", "9454401346", "Senior Officers", false, "👮"),
            
            DirectoryContact("Kotwali Hathras", "Police Station Kotwali City", "9454403741", "Thana", false, "🏢"),
            DirectoryContact("Thana Hathras Gate", "Police Station Hathras Gate", "9454403742", "Thana", false, "🏢"),
            DirectoryContact("Thana Sasni", "Police Station Sasni", "9454403743", "Thana", false, "🏢"),
            DirectoryContact("Thana Sadabad", "Police Station Sadabad", "9454403744", "Thana", false, "🏢"),
            DirectoryContact("Thana Sikandra Rao", "Police Station Sikandra Rao", "9454403745", "Thana", false, "🏢"),
            DirectoryContact("Thana Chandpa", "Police Station Chandpa", "9454403746", "Thana", false, "🏢"),
            DirectoryContact("Thana Hasayan", "Police Station Hasayan", "9454403747", "Thana", false, "🏢"),
            DirectoryContact("Thana Sahpau", "Police Station Sahpau", "9454403748", "Thana", false, "🏢"),
            DirectoryContact("Thana Mursan", "Police Station Mursan", "9454403749", "Thana", false, "🏢"),
            DirectoryContact("Police Lines Hathras", "Reserve Inspector (RI) Police Lines", "9454403750", "Control Room", false, "🏛️")
        )
    }

    fun getRadioGuideItems(): List<RadioCodeItem> {
        return listOf(
            RadioCodeItem("KENWOOD NX3720", "VHF/UHF Mobile & Base Station Digital Transceiver (512 CH, NXDN/FM)", "थाना/कार्यालय और पुलिस वाहनों में स्थापित मुख्य वायरलेस बेस सेट", "Hardware"),
            RadioCodeItem("KENWOOD NX3220", "VHF/UHF Handheld Walkie-Talkie Set with Li-ion Battery (NXDN/FM)", "अधिकारियों और गश्त कर्मियों के हाथ में रहने वाला पोर्टेबल वॉकी-टॉकी सेट", "Hardware"),
            RadioCodeItem("ANI (86XXXX)", "Automatic Number Identification — Radio Caller Identity code", "वायरलेस पर PTT दबाते ही कंट्रोल रूम स्क्रीन पर आने वाला वायरलेस कॉलर नंबर", "Protocol"),
            RadioCodeItem("0 dB GP Antenna", "0 dB Ground Plane Omni-directional Base Station Antenna", "थाने के बेस स्टेशन पर 360-डिग्री स्थानीय वायरलेस कवरेज हेतु प्रयुक्त एंटीना", "Antenna"),
            RadioCodeItem("3 dB GP Antenna", "3 dB High Gain Ground Plane Antenna for Extended Range", "लंबी दूरी की वायरलेस कनेक्टिविटी के लिए उच्च क्षमता वाला बेस एंटीना", "Antenna"),
            RadioCodeItem("0 dB Magnetic Antenna", "Magnetic Vehicle Roof Mount Antenna for NX3720", "पुलिस गश्ती वाहनों (PRV, Bolero, Innova) की छत पर लगा चुंबकीय एंटीना", "Antenna"),
            RadioCodeItem("Whip Antenna", "Flexible Rubber Duck Antenna on NX3220 Handheld", "वॉकी-टॉकी सेट पर लगा लचीला पोर्टेबल एंटीना", "Antenna"),
            RadioCodeItem("ROGER", "Message received and understood", "संदेश प्राप्त हुआ और समझ आ गया", "Procedure"),
            RadioCodeItem("WILCO", "Will comply with received orders", "आदेशानुसार तत्काल कार्रवाई की जाएगी", "Procedure"),
            RadioCodeItem("STAND BY", "Pause transmission, waiting for channel clearance", "प्रतीक्षा करें, चैनल खाली होने दें", "Procedure"),
            RadioCodeItem("SAY AGAIN", "Repeat last transmission", "अंतिम संदेश पुनः दोहराएं", "Procedure"),
            RadioCodeItem("CODE RED", "Immediate tactical emergency / high alert", "आपातकालीन स्थिति / तत्काल पुलिस बल की आवश्यकता", "Procedure"),
            RadioCodeItem("10-4", "Acknowledgement / OK / Affirmative", "संदेश स्वीकार किया / ठीक है", "Ten-Code"),
            RadioCodeItem("10-7", "Out of service / Wireless off the air", "सेट सेवा से बाहर / ड्यूटी समाप्त", "Ten-Code"),
            RadioCodeItem("10-8", "In service / Available for calls on air", "ड्यूटी पर सक्रिय / वायरलेस पर उपलब्ध", "Ten-Code"),
            RadioCodeItem("10-20", "Present Location of unit/vehicle", "वर्तमान स्थान / लोकेशन पूछी या बताई जा रही है", "Ten-Code"),
            RadioCodeItem("10-33", "Emergency traffic only — clear frequency", "आपातकालीन वायरलेस संदेश — केवल आवश्यक बात करें", "Ten-Code")
        )
    }

    private fun norm(v: String?): String {
        return (v ?: "").uppercase(Locale.ROOT).replace(Regex("\\s+"), " ").trim()
    }
}
