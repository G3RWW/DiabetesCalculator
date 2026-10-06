package lt.university.diabetescalculator.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import lt.university.diabetescalculator.R

/**
 * Informacinė juosta viršuje (Top App Bar) su meniu, kuriame yra
 * punktas "Autorius" — jį paspaudus iššoka AlertDialog su autoriaus
 * vardu, pavarde ir grupe (bonus reikalavimas užduotyje).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(currentScreenTitle: String) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showAuthorDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(currentScreenTitle) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Atidaryti meniu")
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
    )

    if (showAuthorDialog) {
        AlertDialog(
            onDismissRequest = { showAuthorDialog = false },
            confirmButton = {
                TextButton(onClick = { showAuthorDialog = false }) { Text("Uždaryti") }
            },
            title = { Text("Apie autorių") },
            text = {
                Column {
                    Text(stringResource(R.string.author_name))
                    Text(stringResource(R.string.author_group))
                }
            }
        )
    }
}
