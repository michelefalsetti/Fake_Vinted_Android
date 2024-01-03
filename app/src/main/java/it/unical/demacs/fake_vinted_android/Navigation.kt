package it.unical.demacs.fake_vinted_android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val route: String, val icon: ImageVector, val stringName: Int) {
    // ... altre voci ...
    HOME("/", Icons.Default.Home, R.string.home),
    ADDITEM("addItem", Icons.Default.Add, R.string.add_item),
    // ... altre voci ...
}