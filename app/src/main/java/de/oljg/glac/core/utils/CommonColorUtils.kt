package de.oljg.glac.core.utils

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun defaultColor() = MaterialTheme.colorScheme.onSurface

@Composable
fun defaultBackgroundColor() = MaterialTheme.colorScheme.surface

@Composable
fun defaultIconButtonColors() = IconButtonDefaults.iconButtonColors(
    contentColor = MaterialTheme.colorScheme.secondary,
    disabledContentColor = MaterialTheme.colorScheme.secondaryContainer
)
