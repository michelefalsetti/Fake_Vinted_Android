package it.unical.demacs.fake_vinted_android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class Routes(val route: String, val icon: ImageVector, val stringName: Int) {
    // ... altre voci ...
    HOME("/", Icons.Default.Home, R.string.home),
    FIRSTPAGE("/firstPage", Icons.Default.Menu,R.string.firstpage),
    ADDITEM("addItem", Icons.Default.Add, R.string.add_item),
    LOGIN("login", Icons.Default.Person, R.string.login),
    REGISTER("login/new", Icons.Default.ExitToApp, R.string.register),
    PROFILE("profile", Icons.Default.Person, R.string.user_page),


}