package com.example.upa_app.presentation.photo

sealed class PhotoNavigationAction {
    object NavigationToSignInDialogAction : PhotoNavigationAction()
    object NavigationToSignOutDialogAction : PhotoNavigationAction( )
    object ShowScheduleUiHints : PhotoNavigationAction()
}