package de.oljg.glac.feature_clock.domain.use_case

import de.oljg.glac.core.util.removeLocalFile

class RemoveImportedFontFile {
    fun execute(importedFileUriStringToRemove: String) {
        removeLocalFile(importedFileUriStringToRemove)
    }
}
