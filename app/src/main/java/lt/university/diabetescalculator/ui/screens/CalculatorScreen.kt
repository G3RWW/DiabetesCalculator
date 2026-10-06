package lt.university.diabetescalculator.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import lt.university.diabetescalculator.data.GlucoseUnit
import lt.university.diabetescalculator.data.TargetProfile
import lt.university.diabetescalculator.ui.theme.Ink
import lt.university.diabetescalculator.ui.theme.NightRaised
import lt.university.diabetescalculator.ui.theme.NumberFamily
import lt.university.diabetescalculator.ui.theme.Paper
import lt.university.diabetescalculator.ui.theme.Persimmon
import lt.university.diabetescalculator.ui.theme.PersimmonBright
import lt.university.diabetescalculator.ui.theme.PlumSoft
import lt.university.diabetescalculator.ui.theme.StatusElevated
import lt.university.diabetescalculator.ui.theme.StatusHigh
import lt.university.diabetescalculator.ui.theme.StatusLow
import lt.university.diabetescalculator.ui.theme.StatusNormal
import lt.university.diabetescalculator.ui.theme.TabShape
import lt.university.diabetescalculator.viewmodel.CalculationUiResult
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

/**
 * Skaičiuoklės ekranas. Rezultatas (insulino dozė) rodomas pačiame viršuje
 * tamsiame "prietaiso ekrane", o įvestis — po juo. Plačiame ekrane (planšetė,
 * horizontali padėtis) įvestis ir rezultatas išdėstomi dviem stulpeliais.
 */
@Composable
fun CalculatorScreen(viewModel: DiabetesViewModel) {
    val result by viewModel.lastResult.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val wide = maxWidth >= 720.dp

        if (wide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    InputSection(viewModel)
                    ParametersSection(viewModel)
                    Spacer(Modifier.height(16.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    DosePanel(result)
                }
            }
        } else {
            val scrollState = rememberScrollState()
            ScrollToTopOnNewResult(result, scrollState)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DosePanel(result)
                InputSection(viewModel)
                ParametersSection(viewModel)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/** Telefone rezultatas yra viršuje — po skaičiavimo grąžiname vartotoją prie jo. */
@Composable
private fun ScrollToTopOnNewResult(result: CalculationUiResult?, scrollState: ScrollState) {
    LaunchedEffect(result?.record?.id) {
        if (result != null) scrollState.animateScrollTo(0)
    }
}

// ---------------------------------------------------------------------------
// Rezultato skydelis
// ---------------------------------------------------------------------------

@Composable
private fun DosePanel(uiResult: CalculationUiResult?) {
    val record = uiResult?.record
    val total by animateFloatAsState(
        targetValue = record?.insulinResult?.totalDoseUnits?.toFloat() ?: 0f,
        animationSpec = tween(600),
        label = "totalDose"
    )
    val statusColor by animateColorAsState(
        targetValue = record?.glycemicStatus?.color ?: PersimmonBright,
        animationSpec = tween(400),
        label = "statusColor"
    )
    val panelColor = if (isSystemInDarkTheme()) NightRaised else Ink
    val onPanel = Paper
    val onPanelSoft = onPanel.copy(alpha = 0.6f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = TabShape,
        color = panelColor,
        contentColor = onPanel
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "REKOMENDUOJAMA DOZĖ",
                    style = MaterialTheme.typography.labelSmall,
                    color = PersimmonBright,
                    modifier = Modifier.weight(1f)
                )
                if (record != null) {
                    Text(record.formattedDate(), style = MaterialTheme.typography.labelSmall, color = onPanelSoft)
                }
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    if (record == null) "—.—" else "%.1f".format(total),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.semantics {
                        contentDescription = if (record == null) "Rezultato dar nėra"
                        else "Iš viso ${record.insulinResult.totalDoseUnits} insulino vienetų"
                    }
                )
                Text(
                    "vnt.",
                    style = MaterialTheme.typography.titleLarge,
                    color = onPanelSoft,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )
            }

            if (uiResult == null || record == null) {
                Text(
                    "Įveskite gliukozės lygį ir angliavandenius, tada paspauskite „Skaičiuoti“.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onPanelSoft
                )
                GlucoseScale(progress = 0f, markerColor = onPanelSoft, description = "Gliukozės skalė, reikšmės dar nėra")
                return@Column
            }

            val meal = record.insulinResult.mealDoseUnits.toFloat()
            val correction = record.insulinResult.correctionDoseUnits.toFloat()
            DoseSplitBar(meal = meal, correction = correction)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                LegendItem(color = PersimmonBright, label = "Maistui", value = meal)
                LegendItem(color = PlumSoft, label = "Korekcijai", value = correction)
            }

            HorizontalDivider(color = onPanel.copy(alpha = 0.12f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusBadge(text = record.glycemicStatus.displayName, color = statusColor)
                Column {
                    Text(record.displayedGlucose(), fontFamily = NumberFamily, fontWeight = FontWeight.Bold)
                    Text(
                        "tikslas %.1f mmol/L".format(record.targetGlucoseMmol),
                        style = MaterialTheme.typography.bodyMedium,
                        color = onPanelSoft
                    )
                }
            }

            GlucoseScale(
                progress = uiResult.glucoseProgress,
                markerColor = statusColor,
                description = "Gliukozės skalė, reikšmė ${record.displayedGlucose()}"
            )

            Text(
                "* Mokomasis skaičiavimas, ne medicininė rekomendacija.",
                style = MaterialTheme.typography.bodyMedium,
                color = onPanelSoft
            )
        }
    }
}

/** Horizontali juosta, parodanti maisto ir korekcijos dozių santykį. */
@Composable
private fun DoseSplitBar(meal: Float, correction: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White.copy(alpha = 0.08f)),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        if (meal > 0f) {
            Box(Modifier.weight(meal).fillMaxHeight().background(PersimmonBright))
        }
        if (correction > 0f) {
            Box(Modifier.weight(correction).fillMaxHeight().background(PlumSoft))
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, value: Float) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("%.1f".format(value), fontFamily = NumberFamily, fontWeight = FontWeight.Bold)
    }
}

/**
 * Gliukozės skalė 0–15 mmol/L: spalvotos zonos (hipo / norma / pakelta / hiper)
 * ir LinearProgressIndicator, rodantis išmatuotą reikšmę.
 */
@Composable
private fun GlucoseScale(progress: Float, markerColor: Color, description: String) {
    // zonų plotis mmol/L vienetais; suma = 15 (visa skalė)
    val zones = listOf(3.9f to StatusLow, 3.9f to StatusNormal, 3.2f to StatusElevated, 4.0f to StatusHigh)
    val ticks = listOf("0", "3.9", "7.8", "11")
    val faint = Paper.copy(alpha = 0.55f)

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            zones.forEach { (width, color) ->
                Box(Modifier.weight(width).fillMaxHeight().background(color))
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            color = markerColor,
            trackColor = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .semantics { contentDescription = description }
        )
        Box(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth()) {
                zones.forEachIndexed { index, (width, _) ->
                    Text(
                        ticks[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = faint,
                        modifier = Modifier.weight(width)
                    )
                }
            }
            Text(
                "15",
                style = MaterialTheme.typography.labelSmall,
                color = faint,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    AssistChip(
        onClick = {},
        label = { Text(text, fontWeight = FontWeight.SemiBold) },
        leadingIcon = { Box(Modifier.size(8.dp).background(color, CircleShape)) },
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f)),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.18f),
            labelColor = Paper
        )
    )
}

// ---------------------------------------------------------------------------
// Įvestis
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(index: String, title: String, trailing: @Composable () -> Unit = {}) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(
            index,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 10.dp)
        )
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun InputSection(viewModel: DiabetesViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(index = "A", title = "Matavimai") {
            UnitDropdown(selected = viewModel.glucoseUnit, onSelect = viewModel::onUnitChange)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ReadingField(
                value = viewModel.glucoseInput,
                onValueChange = viewModel::onGlucoseChange,
                label = "Gliukozė",
                unit = viewModel.glucoseUnit.label,
                error = viewModel.glucoseError,
                modifier = Modifier.weight(1f)
            )
            ReadingField(
                value = viewModel.carbsInput,
                onValueChange = viewModel::onCarbsChange,
                label = "Angliavand.",
                unit = "g",
                error = viewModel.carbsError,
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            FilledTonalButton(
                onClick = viewModel::calculate,
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Skaičiuoti", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(Icons.Filled.ArrowForward, contentDescription = null)
            }
            OutlinedButton(
                onClick = viewModel::clearForm,
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(18.dp),
                contentPadding = ButtonDefaults.TextButtonContentPadding,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onBackground)
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Išvalyti",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun ReadingField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    unit: String,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text(unit, style = MaterialTheme.typography.bodyMedium) },
        textStyle = MaterialTheme.typography.displaySmall,
        singleLine = true,
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(selected: GlucoseUnit, onSelect: (GlucoseUnit) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.width(132.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selected.label,
            onValueChange = {},
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = NumberFamily),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = CircleShape,
            modifier = Modifier
                .menuAnchor()
                .semantics { contentDescription = "Matavimo vienetai: ${selected.label}" }
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            GlucoseUnit.entries.forEach { unit ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(unit.label, fontFamily = NumberFamily) },
                    onClick = {
                        onSelect(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Parametrai
// ---------------------------------------------------------------------------

@Composable
private fun ParametersSection(viewModel: DiabetesViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(index = "B", title = "Parametrai")

        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                LabeledSlider(
                    title = "Angliavandenių santykis",
                    value = "${viewModel.carbRatio.toInt()} g/vnt.",
                    sliderValue = viewModel.carbRatio,
                    onChange = viewModel::onCarbRatioChange,
                    range = 4f..25f,
                    steps = 20,
                    description = "Angliavandenių santykio slankiklis"
                )
                LabeledSlider(
                    title = "Jautrumo faktorius",
                    value = "%.1f mmol/L".format(viewModel.correctionFactor),
                    sliderValue = viewModel.correctionFactor,
                    onChange = viewModel::onCorrectionFactorChange,
                    range = 0.5f..5f,
                    steps = 8,
                    description = "Korekcijos faktoriaus slankiklis"
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    "TIKSLINIS PROFILIS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(
                    modifier = Modifier.selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TargetProfile.entries.forEach { profile ->
                        ProfileOption(
                            profile = profile,
                            selected = viewModel.targetProfile == profile,
                            onSelect = { viewModel.onTargetProfileChange(profile) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledSlider(
    title: String,
    value: String,
    sliderValue: Float,
    onChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    description: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(
                value,
                fontFamily = NumberFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = onChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = Persimmon,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.semantics { contentDescription = description }
        )
    }
}

@Composable
private fun ProfileOption(profile: TargetProfile, selected: Boolean, onSelect: () -> Unit) {
    val borderColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "profileBorder"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .border(1.5.dp, borderColor, MaterialTheme.shapes.small)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent)
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(end = 14.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(12.dp)
        )
        Text(profile.label.substringBefore(" ("), modifier = Modifier.weight(1f))
        Text(
            "%.1f".format(profile.targetMmol),
            fontFamily = NumberFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
