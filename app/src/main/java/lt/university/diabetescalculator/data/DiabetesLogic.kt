package lt.university.diabetescalculator.data

import androidx.compose.ui.graphics.Color
import lt.university.diabetescalculator.ui.theme.StatusElevated
import lt.university.diabetescalculator.ui.theme.StatusHigh
import lt.university.diabetescalculator.ui.theme.StatusLow
import lt.university.diabetescalculator.ui.theme.StatusNormal
import kotlin.math.roundToInt

enum class GlucoseUnit(val label: String) {
    MMOL_L("mmol/L"),
    MG_DL("mg/dL")
}

enum class GlycemicStatus(val displayName: String, val color: Color) {
    LOW("Hipoglikemija", StatusLow),
    NORMAL("Norma", StatusNormal),
    ELEVATED("Pakelta gliukozė", StatusElevated),
    HIGH("Hiperglikemija", StatusHigh)
}

enum class TargetProfile(val label: String, val targetMmol: Double) {
    STRICT("Griežtas (5.0 mmol/L)", 5.0),
    STANDARD("Standartinis (6.0 mmol/L)", 6.0),
    RELAXED("Atpalaiduotas (7.0 mmol/L)", 7.0)
}

/** mg/dL -> mmol/L */
fun mgDlToMmol(mgDl: Double): Double = mgDl / 18.0

/** mmol/L -> mg/dL */
fun mmolToMgDl(mmol: Double): Double = mmol * 18.0

fun classifyGlucose(glucoseMmol: Double): GlycemicStatus = when {
    glucoseMmol < 3.9 -> GlycemicStatus.LOW
    glucoseMmol <= 7.8 -> GlycemicStatus.NORMAL
    glucoseMmol <= 11.0 -> GlycemicStatus.ELEVATED
    else -> GlycemicStatus.HIGH
}

/**
 * Apskaičiuoja rekomenduojamą insulino dozę (vienetais), naudojant
 * angliavandenių skaičiavimo (carb counting) + korekcijos dozės metodą:
 *
 *   maistoDozė = angliavandeniaiGramais / angliavandenių_santykis
 *   korekcijaDozė = max(0, (dabartinėGliukozė - tikslinėGliukozė) / jautrumoFaktorius)
 *   bendraDozė = maistoDozė + korekcijaDozė
 *
 * Korekcijos dozė niekada nėra neigiama — jei gliukozė žemesnė už tikslą,
 * papildomo insulino nereikia.
 */
fun calculateInsulinDose(
    carbsGrams: Double,
    carbRatio: Double,
    currentGlucoseMmol: Double,
    targetGlucoseMmol: Double,
    correctionFactor: Double
): InsulinResult {
    val mealDose = if (carbRatio > 0) carbsGrams / carbRatio else 0.0
    val rawCorrection = if (correctionFactor > 0) {
        (currentGlucoseMmol - targetGlucoseMmol) / correctionFactor
    } else 0.0
    val correctionDose = rawCorrection.coerceAtLeast(0.0)
    val total = mealDose + correctionDose
    return InsulinResult(
        mealDoseUnits = roundToOneDecimal(mealDose),
        correctionDoseUnits = roundToOneDecimal(correctionDose),
        totalDoseUnits = roundToOneDecimal(total)
    )
}

data class InsulinResult(
    val mealDoseUnits: Double,
    val correctionDoseUnits: Double,
    val totalDoseUnits: Double
)

fun roundToOneDecimal(value: Double): Double = (value * 10.0).roundToInt() / 10.0

/**
 * Gliukozės reikšmės padėtis 0..1 skalėje (naudojama LinearProgressIndicator),
 * kur 0 atitinka 0 mmol/L, o 1 atitinka 15 mmol/L.
 */
fun glucoseScaleProgress(glucoseMmol: Double): Float =
    (glucoseMmol / 15.0).coerceIn(0.0, 1.0).toFloat()

/** Validacijos rezultatas vartotojo įvesties laukui. */
sealed class FieldValidation {
    data object Valid : FieldValidation()
    data class Invalid(val message: String) : FieldValidation()
}

fun validatePositiveNumber(raw: String, fieldName: String): FieldValidation {
    if (raw.isBlank()) return FieldValidation.Invalid("$fieldName negali būti tuščias")
    val value = raw.replace(',', '.').toDoubleOrNull()
        ?: return FieldValidation.Invalid("$fieldName turi būti skaičius")
    if (value <= 0.0) return FieldValidation.Invalid("$fieldName turi būti teigiamas")
    return FieldValidation.Valid
}

fun validateGlucose(raw: String, unit: GlucoseUnit): FieldValidation {
    if (raw.isBlank()) return FieldValidation.Invalid("Gliukozės lygis negali būti tuščias")
    val value = raw.replace(',', '.').toDoubleOrNull()
        ?: return FieldValidation.Invalid("Gliukozės lygis turi būti skaičius")
    val maxAllowed = if (unit == GlucoseUnit.MMOL_L) 40.0 else 720.0
    if (value <= 0.0 || value > maxAllowed) {
        return FieldValidation.Invalid("Įveskite realią reikšmę (0 - $maxAllowed ${unit.label})")
    }
    return FieldValidation.Valid
}
