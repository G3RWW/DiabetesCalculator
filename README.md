# Diabeto Skaičiuoklė (Android / Jetpack Compose)

Mokomasis Android apps'as, sukurtas naudojant Kotlin + Jetpack Compose.
Apps'as padeda įvertinti gliukozės lygio kategoriją ir apskaičiuoti
orientacinę insulino dozę pagal angliavandenių kiekį (carb counting
metodas). **Tai mokomasis projektas, ne medicininis įrankis.**

## ⚠️ Prieš atiduodami darbą

Faile `app/src/main/res/values/strings.xml` pakeiskite:

```xml
<string name="author_name">Autorius: Vardenis Pavardenis</string>
<string name="author_group">Grupė: IFxx-x</string>
```

į savo tikrą vardą, pavardę ir grupę. Šis tekstas rodomas programoje per
Top App Bar meniu punktą **"Autorius"**.

## Kaip atsidaryti projektą

1. Atsisiųskite ir išskleiskite archyvą.
2. Android Studio: **File → Open** → pasirinkite `DiabetesCalculator` aplanką.
3. Android Studio pasiūlys sukurti/atnaujinti Gradle wrapper — sutikite
   (šiame archyve nėra `gradle-wrapper.jar` dvejetainio failo, nes jo
   negalima sugeneruoti be paties Gradle; Android Studio jį sukurs
   automatiškai atidarius projektą arba paleidus `gradle wrapper`).
4. Paleiskite ant emuliatoriaus ar telefono (min. Android 8.0 / API 26).

## Reikalavimų atitikimas

### Baziniai 6 balai

- **Vartotojo įvesties apdorojimas** — `DiabetesViewModel` validuoja visus
  laukus (`validatePositiveNumber`, `validateGlucose`), skaičiuoja
  gliukozės kategoriją ir insulino dozę (`DiabetesLogic.kt`), reaguoja į
  mygtukų paspaudimus, slankiklių, jungiklių ir dropdown pasirinkimus.
- **Grafiniai elementai (kuo daugiau realiai panaudotų)**:
  `OutlinedTextField`, `Slider`, `RadioButton`, `ExposedDropdownMenuBox`/
  `DropdownMenu`, `FilledTonalButton`, `OutlinedButton`, `TextButton`,
  `Card`/`ElevatedCard`, `AssistChip`, `FilterChip`, `LinearProgressIndicator`,
  `LazyColumn`, `LazyVerticalGrid`, `Switch`, `Checkbox`, `Snackbar`,
  `TopAppBar`, `NavigationBar`, `NavigationRail`, `AlertDialog`,
  `IconButton`, `Divider`. Kiekvienas elementas susietas su ViewModel
  būsena arba funkcija — nė vienas nėra vien dekoratyvus.
- **Estetika ir išdėstymas** — vieninga Material 3 tema (`ui/theme`),
  suderinta spalvų paletė, nuoseklūs tarpai (`Arrangement.spacedBy`),
  spalvomis koduojami būsenos ženkleliai (žalia/geltona/oranžinė/raudona).
- **Keli ekranai + pilna navigacija** — `navigation-compose` su trimis
  ekranais (Skaičiuoklė / Istorija / Info), bendra `NavHost`, apačios
  juosta arba šoninis skydelis priklausomai nuo ekrano dydžio.
- **Autoriaus duomenys** — žr. skyrių aukščiau.

### Papildomi 2 balai

- **Testai (1 balas)** — vienetiniai testai
  `app/src/test/java/.../DiabetesLogicTest.kt`, testuojantys gliukozės
  klasifikacijos ribas, vienetų konversiją ir insulino dozės skaičiavimą
  (ypač atvejį, kai korekcijos dozė negali būti neigiama).
  Pasirinkimo motyvacija aprašyta paties testo failo komentare. Papildomai
  pridėtas ir instrumentinis navigacijos testas
  (`app/src/androidTest/.../NavigationTest.kt`).
- **Kintami ekrano dydžiai (1 balas)** — `calculateWindowSizeClass` +
  `MainScaffold.kt`: kompaktiškame ekrane rodoma apačios navigacijos
  juosta (`NavigationBar`), platesniame (planšetė / horizontali padėtis) —
  šoninis navigacijos skydelis (`NavigationRail`).
- **Top App Bar su "Autorius" meniu (1 balas)** —
  `ui/components/AppTopBar.kt`: meniu punktas "Autorius" atidaro
  `AlertDialog` su autoriaus vardu, pavarde ir grupe.

## Projekto struktūra

```
app/src/main/java/lt/university/diabetescalculator/
├── MainActivity.kt          # įėjimo taškas, windowSizeClass
├── MainScaffold.kt          # Scaffold + adaptyvi navigacija + Snackbar
├── data/                    # gryna skaičiavimo logika (testuojama)
├── viewmodel/                # DiabetesViewModel — visa būsena ir logika
├── navigation/                # NavHost ir ekranų sąrašas
└── ui/
    ├── theme/                # spalvos, tipografija, formos
    ├── components/           # TopAppBar, apačios juosta / šoninis skydelis
    └── screens/               # Calculator / History / Info ekranai
```

## Pritaikymas naudotojams su ribotomis galimybėmis

- Visi interaktyvūs elementai (mygtukai, IconButton, Slider) turi
  `contentDescription` arba tekstinę žymą ekrano skaitytuvams.
- Spalvų kodavimas visada dubliuojamas tekstu (pvz. "Hiperglikemija"), o
  ne perduodamas vien spalva.
- Pakankamo dydžio palietimo taikiniai (Material 3 numatytieji dydžiai).
