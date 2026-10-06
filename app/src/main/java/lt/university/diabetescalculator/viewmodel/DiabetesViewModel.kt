package lt.university.diabetescalculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lt.university.diabetescalculator.data.CalculationRecord
import lt.university.diabetescalculator.data.FieldValidation
import lt.university.diabetescalculator.data.GlucoseUnit
import lt.university.diabetescalculator.data.TargetProfile
import lt.university.diabetescalculator.data.calculateInsulinDose
import lt.university.diabetescalculator.data.classifyGlucose
import lt.university.diabetescalculator.data.glucoseScaleProgress
import lt.university.diabetescalculator.data.mgDlToMmol
import lt.university.diabetescalculator.data.validateGlucose
import lt.university.diabetescalculator.data.validatePositiveNumber

data class CalculationUiResult(
    val record: CalculationRecord,
    val glucoseProgress: Float // 0f..1f skalei nuo 0 iki 15 mmol/L, naudojama LinearProgressIndicator
)
class DiabetesViewModel : ViewModel() {

    var glucoseInput by mutableStateOf("6.5")
        private set
    var carbsInput by mutableStateOf("60")
        private set
    var glucoseUnit by mutableStateOf(GlucoseUnit.MMOL_L)
        private set
    var carbRatio by mutableStateOf(10f) // gramai angliavandenių vienam insulino vienetui
        private set
    var correctionFactor by mutableStateOf(2f) // mmol/L, kiek 1 insulino vienetas sumažina gliukozę
        private set
    var targetProfile by mutableStateOf(TargetProfile.STANDARD)
        private set
    var remindersEnabled by mutableStateOf(true)
        private set

    var glucoseError by mutableStateOf<String?>(null)
        private set
    var carbsError by mutableStateOf<String?>(null)
        private set

    private val _lastResult = MutableStateFlow<CalculationUiResult?>(null)
    val lastResult: StateFlow<CalculationUiResult?> = _lastResult.asStateFlow()

    private val _history = MutableStateFlow<List<CalculationRecord>>(emptyList())
    val history: StateFlow<List<CalculationRecord>> = _history.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var nextId = 1L

    fun onGlucoseChange(value: String) { glucoseInput = value }
    fun onCarbsChange(value: String) { carbsInput = value }
    fun onCarbRatioChange(value: Float) { carbRatio = value }
    fun onCorrectionFactorChange(value: Float) { correctionFactor = value }
    fun onTargetProfileChange(profile: TargetProfile) { targetProfile = profile }
    fun onRemindersToggle(enabled: Boolean) { remindersEnabled = enabled }

    fun onUnitChange(unit: GlucoseUnit) {
        if (unit == glucoseUnit) return
        // konvertuojame įvestą reikšmę, kad vartotojas nepamestų duomenų keisdamas vienetus
        val current = glucoseInput.replace(',', '.').toDoubleOrNull()
        if (current != null) {
            glucoseInput = if (unit == GlucoseUnit.MG_DL) {
                "%.0f".format(current * 18.0)
            } else {
                "%.1f".format(current / 18.0)
            }
        }
        glucoseUnit = unit
    }

    fun clearForm() {
        glucoseInput = ""
        carbsInput = ""
        glucoseError = null
        carbsError = null
        _lastResult.value = null
    }

    fun consumeSnackbarMessage() {
        _snackbarMessage.value = null
    }

    /** Patikrina visus laukus, ir jei validu — paskaičiuoja bei prideda į istoriją. */
    fun calculate() {
        val glucoseValidation = validateGlucose(glucoseInput, glucoseUnit)
        val carbsValidation = validatePositiveNumber(carbsInput, "Angliavandeniai")

        glucoseError = (glucoseValidation as? FieldValidation.Invalid)?.message
        carbsError = (carbsValidation as? FieldValidation.Invalid)?.message

        val allValid = listOf(glucoseValidation, carbsValidation).all { it is FieldValidation.Valid }
        if (!allValid) {
            _snackbarMessage.value = "Ištaisykite pažymėtus laukus"
            return
        }

        val rawGlucose = glucoseInput.replace(',', '.').toDouble()
        val glucoseMmol = if (glucoseUnit == GlucoseUnit.MMOL_L) rawGlucose else mgDlToMmol(rawGlucose)
        val carbs = carbsInput.replace(',', '.').toDouble()

        val glycemicStatus = classifyGlucose(glucoseMmol)
        val insulin = calculateInsulinDose(
            carbsGrams = carbs,
            carbRatio = carbRatio.toDouble(),
            currentGlucoseMmol = glucoseMmol,
            targetGlucoseMmol = targetProfile.targetMmol,
            correctionFactor = correctionFactor.toDouble()
        )

        val record = CalculationRecord(
            id = nextId++,
            timestamp = System.currentTimeMillis(),
            glucoseMmol = glucoseMmol,
            glucoseUnit = glucoseUnit,
            glycemicStatus = glycemicStatus,
            targetGlucoseMmol = targetProfile.targetMmol,
            insulinResult = insulin
        )

        _lastResult.value = CalculationUiResult(record, glucoseScaleProgress(glucoseMmol))
        _history.update { listOf(record) + it }
        _snackbarMessage.value = "Rezultatas išsaugotas istorijoje"
    }

    fun deleteRecord(id: Long) {
        _history.update { list -> list.filterNot { it.id == id } }
        _snackbarMessage.value = "Įrašas pašalintas"
    }

    fun clearHistory() {
        _history.update { emptyList() }
        _snackbarMessage.value = "Istorija išvalyta"
    }
}
