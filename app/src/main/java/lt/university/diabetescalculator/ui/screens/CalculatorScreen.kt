package lt.university.diabetescalculator.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import lt.university.diabetescalculator.data.GlucoseUnit
import lt.university.diabetescalculator.data.TargetProfile
import lt.university.diabetescalculator.viewmodel.CalculationUiResult
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: DiabetesViewModel) {
    val result by viewModel.lastResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Įveskite duomenis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        UnitDropdown(
            selected = viewModel.glucoseUnit,
            onSelect = viewModel::onUnitChange
        )

        OutlinedTextField(
            value = viewModel.glucoseInput,
            onValueChange = viewModel::onGlucoseChange,
            label = { Text("Gliukozės lygis (${viewModel.glucoseUnit.label})") },
            singleLine = true,
            isError = viewModel.glucoseError != null,
            supportingText = { viewModel.glucoseError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.carbsInput,
            onValueChange = viewModel::onCarbsChange,
            label = { Text("Valgomi angliavandeniai (g)") },
            singleLine = true,
            isError = viewModel.carbsError != null,
            supportingText = { viewModel.carbsError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Divider()

        Text("Nustatymai", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        Column {
            Text("Angliavandenių santykis: ${viewModel.carbRatio.toInt()} g / vnt.")
            Slider(
                value = viewModel.carbRatio,
                onValueChange = viewModel::onCarbRatioChange,
                valueRange = 4f..25f,
                steps = 20,
                modifier = Modifier.semantics { contentDescription = "Angliavandenių santykio slankiklis" }
            )
        }

        Column {
            Text("Jautrumo (korekcijos) faktorius: ${"%.1f".format(viewModel.correctionFactor)} mmol/L")
            Slider(
                value = viewModel.correctionFactor,
                onValueChange = viewModel::onCorrectionFactorChange,
                valueRange = 0.5f..5f,
                steps = 8,
                modifier = Modifier.semantics { contentDescription = "Korekcijos faktoriaus slankiklis" }
            )
        }

        Text("Tikslinis gliukozės profilis", style = MaterialTheme.typography.labelLarge)
        TargetProfile.entries.forEach { profile ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = viewModel.targetProfile == profile,
                    onClick = { viewModel.onTargetProfileChange(profile) }
                )
                Text(profile.label)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledTonalButton(onClick = viewModel::calculate) {
                Text("Skaičiuoti")
            }
            OutlinedButton(onClick = viewModel::clearForm) {
                Text("Išvalyti")
            }
        }

        AnimatedVisibility(visible = result != null) {
            result?.let { ResultCard(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(selected: GlucoseUnit, onSelect: (GlucoseUnit) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            readOnly = true,
            value = selected.label,
            onValueChange = {},
            label = { Text("Matavimo vienetai") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            GlucoseUnit.entries.forEach { unit ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(unit.label) },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ResultCard(uiResult: CalculationUiResult) {
    val record = uiResult.record
    val statusColor by animateColorAsState(
        targetValue = record.glycemicStatus.color,
        animationSpec = tween(400),
        label = "statusColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Rezultatas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(text = record.glycemicStatus.displayName, color = statusColor)
            }

            Text("Gliukozė: ${record.displayedGlucose()} (tikslas: %.1f mmol/L)".format(record.targetGlucoseMmol))
            LinearProgressIndicator(
                progress = { uiResult.glucoseProgress },
                color = statusColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Gliukozės skalė, reikšmė ${record.displayedGlucose()}" }
            )

            Divider()

            Text("Rekomenduojama insulino dozė", fontWeight = FontWeight.SemiBold)
            Text("Maisto dozė: ${record.insulinResult.mealDoseUnits} vnt.")
            Text("Korekcijos dozė: ${record.insulinResult.correctionDoseUnits} vnt.")
            Text(
                "Iš viso: ${record.insulinResult.totalDoseUnits} vnt.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "* Mokomasis skaičiavimas, ne medicininė rekomendacija.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    androidx.compose.material3.AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        )
    )
}
