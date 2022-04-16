package com.example.upa_app.domain.signin

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.upa_app.data.signin.datasources.AuthStateUserDataSource
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * A configurable [RegisteredUserDataSource] used for staging.
 *
 * @see LoginModule
 */
class StagingRegisteredUserDataSource(val isRegistered: Boolean) : RegisteredUserDataSource {
    private val userChanges = MutableStateFlow(Success(isRegistered))

    override fun observeUserChanges(userId: String): Flow<Result<Boolean?>> = userChanges
}

/**
 * A configurable [AuthenticatedUserInfo] used for staging.
 *
 * @see [LoginModule]
 */
open class StagingAuthenticatedUserInfo(
    val context: Context,
    val registered: Boolean = true,
    val signedIn: Boolean = true,
    val userId: String? = "StagingUser"
) : AuthenticatedUserInfo {

    override fun isSignedIn(): Boolean = signedIn

    override fun isRegistered(): Boolean = registered

    override fun isRegistrationDataReady(): Boolean = true

    override fun getEmail(): String? = "staginguser@example.com"

    override fun getProviderData(): MutableList<out UserInfo>? = TODO("Not implemented")

    override fun isAnonymous(): Boolean? = !signedIn

    override fun getPhoneNumber(): String? = TODO("Not implemented")

    override fun getUid(): String? = userId

    override fun isEmailVerified(): Boolean? = TODO("Not implemented")

    override fun getDisplayName(): String? = "Staging User"

    override fun getPhotoUrl(): Uri? {
        val resources = context.resources
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.staging_user_profile))
            .appendPath(resources.getResourceTypeName(R.drawable.staging_user_profile))
            .appendPath(resources.getResourceEntryName(R.drawable.staging_user_profile))
            .build()
    }

    override fun getProviderId(): String? = TODO("Not implemented")

    override fun getLastSignInTimestamp(): Long? = TODO("Not implemented")

    override fun getCreationTimestamp(): Long? = TODO("Not implemented")
}

/**
 * A configurable [AuthStateUserDataSource] used for staging.
 *
 * @see LogingModule
 */
class StagingAuthStateUserDataSource(
    val isSignedIn: Boolean,
    val isRegistered: Boolean,
    val userId: String?,
    val context: Context,
    val notificationAlarmUpdater: NotificationAlarmUpdater
) : AuthStateUserDataSource {

    private val userInfo = MutableStateFlow(
        Success(
            StagingAuthenticatedUserInfo(
                registered = isRegistered,
                signedIn = isSignedIn,
                context = context
            )
        )
    )

    override fun getBasicUserInfo(): Flow<Result<AuthenticatedUserInfoBasic?>> {
        userId?.let {
            notificationAlarmUpdater.updateAll(userId)
        }

        return userInfo
    }
}