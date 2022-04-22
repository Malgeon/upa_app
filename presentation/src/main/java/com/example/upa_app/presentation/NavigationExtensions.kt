package com.example.upa_app.presentation

import android.os.Bundle
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigationrail.NavigationRailView
import java.lang.ref.WeakReference

/**
 * Like BottomNavigationView.setupWithnavController, but for NavigationRailView.
 * TODO(jdkoren): A future release of material will combine the listeners (since both views have a
 * common superclass), which will make this unnecessary.
 */
fun NavigationRailView.setupWithNavController(navController: NavController) {
    setOnItemSelectedListener { item ->
        NavigationUI.onNavDestinationSelected(item, navController)
    }
    val weakRef = WeakReference(this)
    val listener = object : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            val view = weakRef.get()
            if (view == null) {
                navController.removeOnDestinationChangedListener(this)
            } else {
                view.menu.forEach { item ->
                    if (matchNavDestination(destination, item.itemId)) {
                        item.isChecked = true
                    }
                }
            }
        }
    }
    navController.addOnDestinationChangedListener(listener)
}

/**
 * Copy of package-private method in NavigationUI. TODO(jdkoren): remove when the above is removed.
 */
private fun matchNavDestination(destination: NavDestination, id: Int): Boolean {
    var currentDestination = destination
    while (currentDestination.id != id && currentDestination.parent != null) {
        currentDestination = currentDestination.parent as NavDestination
    }
    return currentDestination.id == id
}
