package de.oljg.glac.feature_about.data

import de.oljg.glac.feature_about.ui.utils.AboutScreenDefaults.AUTHOR_NAME

interface ExternalResourceInfo {
    val title: String
    val authors: List<String>
    val sourceName: String
    val sourceUriString: String
    val licenceName: String
    val licenceUriString: String
}


data class ExternalSoundResourceInfo(
    override val title: String,
    override val authors: List<String>,
    override val sourceName: String = "freesound.org", // Most of the sounds come from there
    override val sourceUriString: String,
    override val licenceName: String = "Creative Commons 0", // Most sounds have this licence
    override val licenceUriString: String = "https://creativecommons.org/publicdomain/zero/1.0/",

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
    override val licenceName: String = "SIL Open Font Licence", // Most fonts have this licence
    override val licenceUriString: String
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
    )
)


val externalFonts = listOf(
    ExternalFontResourceInfo(
        title = "D-Din",
        authors = listOf("datto"),
        sourceUriString = "https://www.fontsquirrel.com/fonts/d-din",
        licenceUriString = "https://www.fontsquirrel.com/license/d-din"
    ),
    ExternalFontResourceInfo(
        title = "Exo 2",
        authors = listOf("Natanael Gama"),
        sourceUriString = "https://www.fontsquirrel.com/fonts/exo-2",
        licenceUriString = "https://www.fontsquirrel.com/license/exo-2"
    ),
)
