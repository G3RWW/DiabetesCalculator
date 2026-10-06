package lt.university.diabetescalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import lt.university.diabetescalculator.R

/**
 * Informacinė juosta viršuje (Top App Bar) su meniu, kuriame yra
 * punktas "Autorius" — jį paspaudus iššoka AlertDialog su autoriaus
 * vardu, pavarde ir grupe (bonus reikalavimas užduotyje).
 *
 * Juosta be spalvoto fono: mažas antspaudo tipo užrašas virš didelės
 * serif antraštės, kaip užrašų knygutės puslapio viršus.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(currentScreenTitle: String, screenIndex: Int) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showAuthorDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Text(
                        "DIABETO SKAIČIUOKLĖ · 0${screenIndex + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(currentScreenTitle, style = MaterialTheme.typography.headlineMedium)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        actions = {
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Filled.MoreHoriz, contentDescription = "Atidaryti meniu")
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Autorius") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            showAuthorDialog = true
                        }
                    )
                }
            }
        }
    )

    if (showAuthorDialog) {
        AlertDialog(
            onDismissRequest = { showAuthorDialog = false },
            confirmButton = {
                TextButton(onClick = { showAuthorDialog = false }) { Text("Uždaryti") }
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            title = { Text("Apie autorių", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.author_name), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        stringResource(R.string.author_group),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}
