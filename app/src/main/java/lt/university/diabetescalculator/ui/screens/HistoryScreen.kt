package lt.university.diabetescalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lt.university.diabetescalculator.data.CalculationRecord
import lt.university.diabetescalculator.data.GlycemicStatus
import lt.university.diabetescalculator.ui.theme.NumberFamily
import lt.university.diabetescalculator.ui.theme.TabShapeSmall
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

/**
 * Slenkantis sąrašas (LazyColumn) su visais atliktais skaičiavimais, išdėstytas
 * kaip laiko juosta. Viršuje — suvestinė (įrašų skaičius, vidutinė gliukozė,
 * bendras insulinas). Galima filtruoti pagal gliukozės kategoriją (FilterChip)
 * ir ieškoti pagal datos tekstą (OutlinedTextField), taip pat pašalinti
 * pavienį įrašą arba išvalyti visą istoriją.
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

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                "Skaičiavimų istorija",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = viewModel::clearHistory) { Text("Išvalyti visą istoriją") }
            }
        }

        SummaryStrip(history)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Ieškoti pagal datą (pvz. 2026-09)") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Išvalyti paiešką")
                    }
                }
            },
            singleLine = true,
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeFilter == null,
                onClick = { activeFilter = null },
                label = { Text("Visi") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                    selectedLabelColor = MaterialTheme.colorScheme.background
                )
            )
            GlycemicStatus.entries.forEach { status ->
                FilterChip(
                    selected = activeFilter == status,
                    onClick = { activeFilter = if (activeFilter == status) null else status },
                    label = { Text(status.displayName, maxLines = 1) },
                    leadingIcon = { Box(Modifier.size(8.dp).background(status.color, CircleShape)) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = status.color.copy(alpha = 0.22f))
                )
            }
        }

        if (filtered.isEmpty()) {
            EmptyState(
                title = if (history.isEmpty()) "Tuščias puslapis" else "Nieko nerasta",
                body = if (history.isEmpty()) "Kol kas nėra atliktų skaičiavimų." else "Pagal filtrus nieko nerasta."
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                itemsIndexed(filtered, key = { _, record -> record.id }) { index, record ->
                    TimelineItem(
                        record = record,
                        isFirst = index == 0,
                        isLast = index == filtered.lastIndex,
                        onDelete = { viewModel.deleteRecord(record.id) }
                    )
                }
            }
        }
    }
}

/** Trys suvestinės skaičiai virš sąrašo. */
@Composable
private fun SummaryStrip(history: List<CalculationRecord>) {
    val avgGlucose = history.map { it.glucoseMmol }.average().takeIf { history.isNotEmpty() }
    val totalInsulin = history.sumOf { it.insulinResult.totalDoseUnits }
    val inRange = history.count { it.glycemicStatus == GlycemicStatus.NORMAL }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatTile(
            label = "ĮRAŠAI",
            value = history.size.toString(),
            caption = "$inRange normoje",
            container = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = "VIDURKIS",
            value = avgGlucose?.let { "%.1f".format(it) } ?: "—",
            caption = "mmol/L",
            container = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.weight(1f)
        )
        StatTile(
            label = "Σ DOZĖ",
            value = "%.1f".format(totalInsulin),
            caption = "vnt. iš viso",
            container = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, caption: String, container: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(container, TabShapeSmall)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        Text(value, style = MaterialTheme.typography.displaySmall.copy(fontSize = 26.sp), maxLines = 1)
        Text(caption, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "∅",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Vienas laiko juostos įrašas: kairėje linija su spalvotu tašku, dešinėje kortelė. */
@Composable
private fun TimelineItem(record: CalculationRecord, isFirst: Boolean, isLast: Boolean, onDelete: () -> Unit) {
    val lineColor = MaterialTheme.colorScheme.outlineVariant
    val status = record.glycemicStatus

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // laiko juostos stulpelis
        Box(modifier = Modifier.width(26.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.width(2.dp).height(22.dp).background(if (isFirst) Color.Transparent else lineColor))
                Box(Modifier.width(2.dp).weight(1f).background(if (isLast) Color.Transparent else lineColor))
            }
            Box(
                Modifier
                    .padding(top = 16.dp)
                    .size(14.dp)
                    .background(status.color, CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
            )
        }

        Card(
            modifier = Modifier.weight(1f).padding(start = 6.dp, bottom = 12.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        record.formattedDate(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        record.displayedGlucose(),
                        fontFamily = NumberFamily,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        "${status.displayName} · tikslas %.1f".format(record.targetGlucoseMmol),
                        style = MaterialTheme.typography.bodyMedium,
                        color = status.color
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onBackground, TabShapeSmall)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "${record.insulinResult.totalDoseUnits}",
                        fontFamily = NumberFamily,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.background
                    )
                    Text("vnt.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.background)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Pašalinti įrašą nuo ${record.formattedDate()}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
