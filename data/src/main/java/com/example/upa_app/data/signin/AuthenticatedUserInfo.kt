package com.example.upa_app.data.signin

import android.net.Uri
import com.google.firebase.auth.UserInfo

/**
 * Interface to decouple the user into from Firebase
 *
 * @See [FirebaseRegisteredUserInfo]
 */
interface AuthenticatedUserInfo : AuthenticatedUserInfoBasic, AuthenticatedUserInfoRegistered

/**
 * Basic user info
 */
interface AuthenticatedUserInfoBasic {

    fun isSignedIn(): Boolean

    fun getEmail(): String?

    fun getProviderData(): MutableList<out UserInfo>?

    fun getLastSignInTimestamp(): Long?

    fun getCreationTimestamp(): Long?

    fun isAnonymous(): Boolean?

    fun getPhoneNumber(): String?

    fun getUid(): String?

    fun isEmailVerified(): Boolean?

    fun getDisplayName(): String?

    fun getPhotoUrl(): Uri?

    fun getProviderId(): String?
}

/**
 * Extra information about the auth and registration state of the user.
 */
interface AuthenticatedUserInfoRegistered {

    fun isRegistered(): Boolean

    fun isRegistrationDataReady(): Boolean
}