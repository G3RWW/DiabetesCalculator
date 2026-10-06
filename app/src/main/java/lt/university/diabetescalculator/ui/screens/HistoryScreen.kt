package lt.university.diabetescalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import lt.university.diabetescalculator.data.CalculationRecord
import lt.university.diabetescalculator.data.GlycemicStatus
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel




/**
 * Slenkantis sąrašas (LazyColumn) su visais atliktais skaičiavimais.
 * Galima filtruoti pagal gliukozės kategoriją (FilterChip) ir ieškoti
 * pagal datos tekstą (OutlinedTextField), taip pat pašalinti pavienį
 * įrašą arba išvalyti visą istoriją.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: DiabetesViewModel) {
    val history by viewModel.history.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf<GlycemicStatus?>(null) }

    val filtered = history
        .filter { activeFilter == null || it.glycemicStatus == activeFilter }
        .filter { searchQuery.isBlank() || it.formattedDate().contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Skaičiavimų istorija", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Ieškoti pagal datą (pvz. 2026-09)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeFilter == null,
                onClick = { activeFilter = null },
                label = { Text("Visi") }
            )
            GlycemicStatus.entries.forEach { status ->
                FilterChip(
                    selected = activeFilter == status,
                    onClick = { activeFilter = if (activeFilter == status) null else status },
                    label = { Text(status.displayName, maxLines = 1) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = status.color.copy(alpha = 0.2f))
                )
            }
        }

        if (history.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = viewModel::clearHistory) { Text("Išvalyti visą istoriją") }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (history.isEmpty()) "Kol kas nėra atliktų skaičiavimų." else "Pagal filtrus nieko nerasta.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { record ->
                    HistoryItem(record = record, onDelete = { viewModel.deleteRecord(record.id) })
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(record: CalculationRecord, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.formattedDate(), style = MaterialTheme.typography.labelLarge)
                Text("${record.displayedGlucose()} · ${record.glycemicStatus.displayName}")
                Text("Tikslas: %.1f mmol/L".format(record.targetGlucoseMmol))
                Text("Insulinas: ${record.insulinResult.totalDoseUnits} vnt.")
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Pašalinti įrašą nuo ${record.formattedDate()}"
                )
            }
        }
    }
}
