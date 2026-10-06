package lt.university.diabetescalculator

import lt.university.diabetescalculator.data.FieldValidation
import lt.university.diabetescalculator.data.GlucoseUnit
import lt.university.diabetescalculator.data.GlycemicStatus
import lt.university.diabetescalculator.data.calculateInsulinDose
import lt.university.diabetescalculator.data.classifyGlucose
import lt.university.diabetescalculator.data.glucoseScaleProgress
import lt.university.diabetescalculator.data.mgDlToMmol
import lt.university.diabetescalculator.data.mmolToMgDl
import lt.university.diabetescalculator.data.validateGlucose
import lt.university.diabetescalculator.data.validatePositiveNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vienetiniai testai pagrindinei skaičiavimo logikai (DiabetesLogic.kt).
 *
 * Kodėl testuojami būtent šie atvejai:
 * 1) Gliukozės kategorijų riboms (3.9 / 7.8 / 11.0 mmol/L) — nuo šių verčių
 *    priklauso, kokia spalva ir pavadinimas bus rodomas vartotojui, todėl
 *    neteisinga riba (off-by-one tipo klaida tarp "<" ir "<=") suklaidintų
 *    apie gliukozės būklę.
 * 2) Vienetų konversijai (mmol/L <-> mg/dL) — nes vartotojas gali perjungti
 *    vienetus, ir apvalinimo/formulės klaida iškreiptų visus tolimesnius
 *    skaičiavimus.
 * 3) Insulino dozės skaičiavimui, ypač situacijai, kai gliukozė yra ŽEMESNĖ
 *    už tikslinę reikšmę — svarbu užtikrinti, kad korekcijos dozė NIEKADA
 *    netampa neigiama (tai būtų logiškai ir praktiškai klaidinga
 *    rekomendacija, galinti paskatinti vartotoją susileisti per daug insulino).
 * 4) Gliukozės skalės progreso funkcijai — ji naudojama UI juostai, todėl
 *    reikšmė visada turi likti tarp 0 ir 1, net kai gliukozė viršija skalės
 *    ribas.
 * 5) Įvesties validacijai — kad tušti, neigiami ar netinkami skaičiai būtų
 *    atpažinti prieš atliekant skaičiavimus.
 */
class DiabetesLogicTest {

    // --- Gliukozės klasifikacija ---

    @Test
    fun glucose_justBelowLowBoundary_isLow() {
        assertEquals(GlycemicStatus.LOW, classifyGlucose(3.8))
    }

    @Test
    fun glucose_atNormalLowerBoundary_isNormal() {
        assertEquals(GlycemicStatus.NORMAL, classifyGlucose(3.9))
    }

    @Test
    fun glucose_atNormalUpperBoundary_isStillNormal() {
        assertEquals(GlycemicStatus.NORMAL, classifyGlucose(7.8))
    }

    @Test
    fun glucose_justAboveNormal_isElevated() {
        assertEquals(GlycemicStatus.ELEVATED, classifyGlucose(7.9))
    }

    @Test
    fun glucose_aboveHighBoundary_isHigh() {
        assertEquals(GlycemicStatus.HIGH, classifyGlucose(11.1))
    }

    // --- Vienetų konversija ---

    @Test
    fun unitConversion_mmolToMgDl_isReversible() {
        val original = 6.5
        val converted = mgDlToMmol(mmolToMgDl(original))
        assertEquals(original, converted, 0.0001)
    }

    @Test
    fun unitConversion_knownValue() {
        // 180 mg/dL == 10.0 mmol/L
        assertEquals(10.0, mgDlToMmol(180.0), 0.0001)
    }

    // --- Insulino dozės skaičiavimas ---

    @Test
    fun insulinDose_glucoseBelowTarget_correctionIsNeverNegative() {
        val result = calculateInsulinDose(
            carbsGrams = 40.0,
            carbRatio = 10.0,
            currentGlucoseMmol = 4.0,
            targetGlucoseMmol = 6.0,
            correctionFactor = 2.0
        )
        assertTrue("Korekcijos dozė negali būti neigiama", result.correctionDoseUnits >= 0.0)
        assertEquals(0.0, result.correctionDoseUnits, 0.0001)
        assertEquals(4.0, result.mealDoseUnits, 0.0001)
    }

    @Test
    fun insulinDose_glucoseAboveTarget_addsCorrectionDose() {
        val result = calculateInsulinDose(
            carbsGrams = 60.0,
            carbRatio = 10.0,
            currentGlucoseMmol = 10.0,
            targetGlucoseMmol = 6.0,
            correctionFactor = 2.0
        )
        // mealDose = 60/10 = 6.0 ; correction = (10-6)/2 = 2.0 ; total = 8.0
        assertEquals(6.0, result.mealDoseUnits, 0.0001)
        assertEquals(2.0, result.correctionDoseUnits, 0.0001)
        assertEquals(8.0, result.totalDoseUnits, 0.0001)
    }

    // --- Gliukozės skalės progresas (UI juostai) ---

    @Test
    fun glucoseScaleProgress_isClampedToZeroAndOne() {
        assertEquals(0f, glucoseScaleProgress(-5.0), 0.0001f)
        assertEquals(1f, glucoseScaleProgress(30.0), 0.0001f)
    }

    @Test
    fun glucoseScaleProgress_midValue_isHalf() {
        assertEquals(0.5f, glucoseScaleProgress(7.5), 0.0001f)
    }

    // --- Įvesties validacija ---

    @Test
    fun validation_blankField_isInvalid() {
        val validation = validatePositiveNumber("", "Angliavandeniai")
        assertTrue(validation is FieldValidation.Invalid)
    }

    @Test
    fun validation_negativeNumber_isInvalid() {
        val validation = validatePositiveNumber("-5", "Angliavandeniai")
        assertTrue(validation is FieldValidation.Invalid)
    }

    @Test
    fun validation_validPositiveNumber_isValid() {
        val validation = validatePositiveNumber("72.5", "Angliavandeniai")
        assertEquals(FieldValidation.Valid, validation)
    }

    @Test
    fun validation_glucoseOutOfRealisticRange_isInvalid() {
        val validation = validateGlucose("999", GlucoseUnit.MMOL_L)
        assertTrue(validation is FieldValidation.Invalid)
    }
}
