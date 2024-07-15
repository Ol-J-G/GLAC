package de.oljg.glac.feature_about.data

import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.C_C_BY_4
import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.C_C_BY_NC_3
import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.C_C_BY_NC_4
import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.C_C_SAMPLING_PLUS
import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.C_C_ZERO
import de.oljg.glac.feature_about.data.ExternalResourcesDefaults.SIL_OFL_NAME
import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.AUTHOR_NAME


object ExternalResourcesDefaults {
    private const val C_C_ZERO_NAME = "CC Zero 1.0"
    private const val C_C_ZERO_URL = "https://creativecommons.org/publicdomain/zero/1.0/"
    private const val C_C_BY_NC_3_NAME = "CC BY-NC 3.0"
    private const val C_C_BY_NC_3_URL = "https://creativecommons.org/licenses/by-nc/3.0/"
    private const val C_C_BY_NC_4_NAME = "CC BY-NC 4.0"
    private const val C_C_BY_NC_4_URL = "https://creativecommons.org/licenses/by-nc/4.0/"
    private const val C_C_BY_4_NAME = "CC BY 4.0"
    private const val C_C_BY_4_URL = "https://creativecommons.org/licenses/by/4.0/"
    private const val C_C_SAMPLING_PLUS_NAME = "CC SAMPLING+ 1.0"
    private const val C_C_SAMPLING_PLUS_URL = "https://creativecommons.org/licenses/sampling+/1.0/"

    val C_C_ZERO = LicenceInfo(C_C_ZERO_NAME, C_C_ZERO_URL)
    val C_C_BY_NC_3 = LicenceInfo(C_C_BY_NC_3_NAME, C_C_BY_NC_3_URL)
    val C_C_BY_NC_4 = LicenceInfo(C_C_BY_NC_4_NAME, C_C_BY_NC_4_URL)
    val C_C_BY_4 = LicenceInfo(C_C_BY_4_NAME, C_C_BY_4_URL)
    val C_C_SAMPLING_PLUS = LicenceInfo(C_C_SAMPLING_PLUS_NAME, C_C_SAMPLING_PLUS_URL)

    internal const val SIL_OFL_NAME = "SIL Open Font Licence"
}


data class LicenceInfo(
    val name: String,
    val url: String
)


interface ExternalResourceInfo {
    val title: String
    val authors: List<String>
    val sourceName: String
    val sourceUriString: String
    val licence: LicenceInfo
}


data class ExternalSoundResourceInfo(
    override val title: String,
    override val authors: List<String>,
    override val sourceName: String = "freesound.org", // Most of the sounds come from there
    override val sourceUriString: String,
    override val licence: LicenceInfo = C_C_ZERO, // Most sounds have this licence

    /**
     * All external sounds had to be renamed ("GLAC_" prefix + shorthand) so that users can better
     * distinguish them from the imported and default ringtones.
     */
    val renamedTo: String? = null,

    /**
     * I had to adapt some sounds to make them suitable as alarm sound
     * (e.g. some were too quite, others had too much noise ...)
     */
    val modifiedBy: String? = null,
    val modifications: List<String>? = null // What adjustments have been made
) : ExternalResourceInfo


data class ExternalFontResourceInfo(
    override val title: String,
    override val authors: List<String>,
    override val sourceName: String = "fontsquirrel.com", // Most of the fonts come from there
    override val sourceUriString: String,
    override val licence: LicenceInfo
) : ExternalResourceInfo


val externalSounds = listOf(
    ExternalSoundResourceInfo(
        title = "20191226 - Woodpeckers (mp3)",
        authors = listOf("peter1955"),
        sourceUriString = "https://freesound.org/people/peter1955/sounds/500308/",
        renamedTo = "GLAC_Woodpeckers.mp3"
    ),
    ExternalSoundResourceInfo(
        title = "Tawny Owl in Molkom, Sweden.",
        authors = listOf("Marcuspepsi"),
        sourceUriString = "https://freesound.org/people/Marcuspepsi/sounds/450214/",
        renamedTo = "GLAC_Owl.mp3",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Normalization", "Noise Reduction")
    ),
    ExternalSoundResourceInfo(
        title = "Alarm Bell",
        authors = listOf("SergeQuadrado"),
        sourceUriString = "https://freesound.org/people/SergeQuadrado/sounds/460262/",
        licence = C_C_BY_NC_3,
        renamedTo = "GLAC_TwoBells.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted", "Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Fast Freight Train Passes Crossing with Horn",
        authors = listOf("WeenyBeany"),
        sourceUriString = "https://freesound.org/people/WeenyBeany/sounds/737884/",
        licence = C_C_BY_4,
        renamedTo = "GLAC_Train.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Motorcycle_Startup_Driveaway.wav",
        authors = listOf("StephenSaldanha"),
        sourceUriString = "https://freesound.org/people/StephenSaldanha/sounds/133731/",
        licence = C_C_BY_4,
        renamedTo = "GLAC_Motorcycle.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Zen Plucked Chords, Riff",
        authors = listOf("f-r-a-g-i-l-e"),
        sourceUriString = "https://freesound.org/people/f-r-a-g-i-l-e/sounds/496745/",
        renamedTo = "GLAC_ZenPluckedChords.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted", "Volume-Normalized", "Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "pewitsWater2.WAV",
        authors = listOf("nicStage"),
        sourceUriString = "https://freesound.org/people/nicStage/sounds/95087/",
        licence = C_C_BY_4,
        renamedTo = "GLAC_Water.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "campfire.wav",
        authors = listOf("aerror"),
        sourceUriString = "https://freesound.org/people/aerror/sounds/350757/",
        renamedTo = "GLAC_Campfire.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Kiholo Bay Rocky Break.wav",
        authors = listOf("tombenedict"),
        sourceUriString = "https://freesound.org/people/tombenedict/sounds/397592/",
        licence = C_C_BY_4,
        renamedTo = "GLAC_Waves.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted", "Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Frogs in Alliagtor Creek at 4am.mp3",
        authors = listOf("Bansemer"),
        sourceUriString = "https://freesound.org/people/Bansemer/sounds/35245/",
        renamedTo = "GLAC_Frogs.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted", "Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "ByTheRiver55s.wav",
        authors = listOf("acclivity"),
        sourceUriString = "https://freesound.org/people/acclivity/sounds/13553/",
        licence = C_C_BY_NC_4,
        renamedTo = "GLAC_ByTheRiver.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted", "Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Ambiance_Atmosphere_Interior_Wood_Rain_Thunders_Fire_Loop_Stereo.ogg",
        authors = listOf("Nox_Sound"),
        sourceUriString = "https://freesound.org/people/Nox_Sound/sounds/553887/",
        renamedTo = "GLAC_StormyRain.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Cutted")
    ),
    ExternalSoundResourceInfo(
        title = "Tibetan Chanting ཨོཾ་མ་ཎི་པདྨེ་ཧཱུྃ",
        authors = listOf("djgriffin"),
        sourceUriString = "https://freesound.org/people/djgriffin/sounds/15488/",
        licence = C_C_BY_NC_4,
        renamedTo = "GLAC_TibetanChanting.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "NEPTUN-Solo-07 Tibetan Singing Bowl",
        authors = listOf("the_very_Real_Horst"),
        sourceUriString = "https://freesound.org/people/the_very_Real_Horst/sounds/240934/",
        renamedTo = "GLAC_TibetanSingingBowl.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "temple_bell_002.wav",
        authors = listOf("tec_studio"),
        sourceUriString = "https://freesound.org/people/tec_studio/sounds/668647/",
        renamedTo = "GLAC_JapaneseTempleBell.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    ),
    ExternalSoundResourceInfo(
        title = "Kongourin-ji_bell_mp3.mp3",
        authors = listOf("MShades"),
        sourceUriString = "https://freesound.org/people/MShades/sounds/25914/",
        licence = C_C_SAMPLING_PLUS,
        renamedTo = "GLAC_KongourinJiBell.ogg",
        modifiedBy = AUTHOR_NAME,
        modifications = listOf("Converted to Ogg")
    )
)


val externalFonts = listOf(
    ExternalFontResourceInfo(
        title = "D-Din",
        authors = listOf("datto"),
        sourceUriString = "https://www.fontsquirrel.com/fonts/d-din",
        licence = LicenceInfo(SIL_OFL_NAME, "https://www.fontsquirrel.com/license/d-din")
    ),
    ExternalFontResourceInfo(
        title = "Exo 2",
        authors = listOf("Natanael Gama"),
        sourceUriString = "https://www.fontsquirrel.com/fonts/exo-2",
        licence = LicenceInfo(SIL_OFL_NAME, "https://www.fontsquirrel.com/license/exo-2")
    ),
)
