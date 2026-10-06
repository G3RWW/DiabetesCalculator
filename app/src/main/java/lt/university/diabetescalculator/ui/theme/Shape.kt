package lt.university.diabetescalculator.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Asimetriška "lapelio" forma — vienas aštrus kampas išskiria pagrindinius blokus. */
val TabShape = RoundedCornerShape(topStart = 30.dp, topEnd = 6.dp, bottomEnd = 30.dp, bottomStart = 30.dp)
val TabShapeSmall = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
