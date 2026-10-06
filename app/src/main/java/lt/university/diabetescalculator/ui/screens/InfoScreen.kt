package lt.university.diabetescalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
 * mokomosiomis kortelėmis + keli papildomi valdikliai (Switch, Checkbox),
 * kurie realiai veikia per ViewModel būseną.
 */
@Composable
fun InfoScreen(viewModel: DiabetesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Apie skaičiavimus", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.weight(1f).padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(tips) { tip -> TipCard(tip) }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Nustatymai", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Priminimai matuoti gliukozę")
            Switch(
                checked = viewModel.remindersEnabled,
                onCheckedChange = viewModel::onRemindersToggle
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = viewModel.remindersEnabled,
                onCheckedChange = viewModel::onRemindersToggle
            )
            Text(
                if (viewModel.remindersEnabled) "Priminimai įjungti kasdien 08:00"
                else "Priminimai išjungti"
            )
        }
    }
}

@Composable
private fun TipCard(tip: InfoTip) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(tip.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(tip.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
