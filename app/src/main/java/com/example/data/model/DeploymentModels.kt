package com.example.data.model

data class AntennaInfo(
    val zeroDbGp: String? = null,
    val threeDbGp: String? = null,
    val zeroDbMagnetic: String? = null,
    val whipAntenna: String? = null
) {
    val hasAny: Boolean
        get() = !zeroDbGp.isNullOrBlank() || !threeDbGp.isNullOrBlank() ||
                !zeroDbMagnetic.isNullOrBlank() || !whipAntenna.isNullOrBlank()
}

data class AniRecord(
    val slNo: Int,
    val setNumber: String?,
    val ani: String?,
    val whereDeployed: String? = null,
    val pdfPage: Int? = null,
    val source: String? = null
)

data class GplDeployment(
    val slNo: Int,
    val pdfPage: String,
    val location: String?,
    val stMobSetNX3720: String?,
    val antenna: AntennaInfo,
    val powerSupply: String?,
    val battery12V: String?,
    val tMast: String?,
    val giPole: String?,
    val pneumaticMast: String?,
    val hhSetNX3220: String?,
    val suBattsCharger: String?,
    val paSystem: String?,
    val remarks: String?,
    val equipmentCodes: List<String>,
    val aniRecords: List<AniRecord>,
    val aniOnly: Boolean = false
) {
    val displayTitle: String
        get() = location?.ifBlank { null } ?: "Unknown Location / ANI Only"

    val uniqueKey: String
        get() = "$slNo|$pdfPage|${location ?: ""}|${equipmentCodes.joinToString()}"
}

enum class SearchMode(val displayName: String, val icon: String, val hintText: String, val placeholder: String) {
    SET("Set Number", "🔢", "Exact और partial Set Number / Equipment Code search", "उदाहरण: C4110644"),
    ANI("ANI Number", "📞", "Exact और partial ANI Number search", "उदाहरण: 861029 या 865055"),
    PLACE("जगह / थाना", "📍", "थाना/जगह का पूरा या partial नाम search", "उदाहरण: KOTWALI या SASNI"),
    ALL("सभी", "🔎", "एक ही box से Set Number, ANI Number और जगह/थाना search करें", "Set Number, ANI या जगह — सभी search करें")
}

enum class FilterCategory(val label: String, val icon: String) {
    ALL("All", "⚡"),
    THANA("Thana / Kotwali", "🏢"),
    OFFICERS("Officers & Escorts", "⭐"),
    MOBILES("Vehicles / Mobiles", "🚓"),
    BASE_MAST("Base Mast / Poles", "🗼"),
    HANDHELD("NX3220 Walkie-Talkie", "📻"),
    BASE_SET("NX3720 Base/Mob", "📡"),
    PA_SYSTEM("PA System", "📢")
}

data class DirectoryContact(
    val title: String,
    val designation: String,
    val number: String,
    val category: String, // "Control Room", "Senior Officers", "Emergency Helplines", "Thana"
    val isTollFree: Boolean = false,
    val icon: String = "📞"
)

data class RadioCodeItem(
    val code: String,
    val meaning: String,
    val hindiMeaning: String,
    val category: String // "Procedure", "Ten-Code", "Emergency"
)
