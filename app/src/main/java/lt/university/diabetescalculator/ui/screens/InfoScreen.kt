package lt.university.diabetescalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import lt.university.diabetescalculator.ui.theme.Ink
import lt.university.diabetescalculator.ui.theme.NightRaised
import lt.university.diabetescalculator.ui.theme.Paper
import lt.university.diabetescalculator.ui.theme.PersimmonBright
import lt.university.diabetescalculator.ui.theme.TabShape
import lt.university.diabetescalculator.ui.theme.TabShapeSmall
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

private data class InfoTip(val title: String, val body: String)

private val tips = listOf(
    InfoTip("Angliavandenių skaičiavimas", "Insulino dozė maistui priklauso nuo suvalgomų angliavandenių ir individualaus santykio (g/vnt.)."),
    InfoTip("Insulino veikimo laikas", "Greitai veikiantis insulinas pradeda veikti per 10-20 min. ir veikia apie 3-5 val."),
    InfoTip("Korekcijos dozė", "Naudojama, kai gliukozės lygis viršija tikslinę reikšmę — apskaičiuojama pagal jautrumo faktorių."),
    InfoTip("Hipoglikemija", "Gliukozės lygis žemiau 3.9 mmol/L. Gali kelti pavojų, reikalinga greita reakcija."),
    InfoTip("Hiperglikemija", "Ilgai išlikęs aukštas gliukozės lygis gali sukelti komplikacijas."),
    InfoTip("Reguliarus matavimas", "Nuolatinis gliukozės stebėjimas padeda geriau valdyti insulino dozes."),
)

/**
 * Informacinis ekranas: slenkantis tinklelis (LazyVerticalGrid) su
 * sunumeruotomis mokomosiomis kortelėmis + keli papildomi valdikliai
 * (Switch, Checkbox), kurie realiai veikia per ViewModel būseną.
 * Antraštė ir nustatymai užima visą tinklelio plotį, todėl visas
 * ekranas slenka kaip vienas puslapis.
 */
@Composable
fun InfoScreen(viewModel: DiabetesViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 156.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Apie skaičiavimus",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        itemsIndexed(tips) { index, tip -> TipCard(index, tip) }

        item(span = { GridItemSpan(maxLineSpan) }) {
            RemindersCard(viewModel)
        }
    }
}

@Composable
private fun TipCard(index: Int, tip: InfoTip) {
    // kortelės spalvos sukasi, kad tinklelis neatrodytų monotoniškai
    val dark = isSystemInDarkTheme()
    val (container, content) = when (index % 4) {
        0 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        2 -> (if (dark) NightRaised else Ink) to Paper
        else -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    }

    Card(
        modifier = Modifier.heightIn(min = 180.dp),
        shape = if (index % 2 == 0) TabShapeSmall else MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "%02d".format(index + 1),
                style = MaterialTheme.typography.displaySmall,
                color = if (index % 4 == 2) PersimmonBright else content.copy(alpha = 0.35f)
            )
            Text(tip.title, style = MaterialTheme.typography.titleMedium)
            Text(tip.body, style = MaterialTheme.typography.bodyMedium, color = content.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun RemindersCard(viewModel: DiabetesViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        shape = TabShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "NUSTATYMAI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text(
                    "Priminimai matuoti gliukozę",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                )
                Switch(
                    checked = viewModel.remindersEnabled,
                    onCheckedChange = viewModel::onRemindersToggle,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color.Transparent
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.remindersEnabled,
                    onCheckedChange = viewModel::onRemindersToggle,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    if (viewModel.remindersEnabled) "Priminimai įjungti kasdien 08:00"
                    else "Priminimai išjungti",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
