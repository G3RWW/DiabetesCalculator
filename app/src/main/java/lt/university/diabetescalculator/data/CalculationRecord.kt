package lt.university.diabetescalculator.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Vienas išsaugotas skaičiavimas, rodomas Istorijos ekrano sąraše. */
data class CalculationRecord(
    val id: Long,
    val timestamp: Long,
    val glucoseMmol: Double,
    val glucoseUnit: GlucoseUnit,
    val glycemicStatus: GlycemicStatus,
    val targetGlucoseMmol: Double,
    val insulinResult: InsulinResult
) {
    fun formattedDate(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))

    fun displayedGlucose(): String {
        val value = if (glucoseUnit == GlucoseUnit.MMOL_L) glucoseMmol else mmolToMgDl(glucoseMmol)
        return "%.1f %s".format(value, glucoseUnit.label)
    }
}
