package com.example.upa_app.data.unsplash

import com.example.upa_app.data.UpdateSource
import com.example.upa_app.data.db.UnsplashDao
import com.example.upa_app.data.db.UnsplashEntity
import com.example.upa_app.model.unsplash.UnsplashPhotoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoRepository @Inject constructor(
    private val remoteDataSource: UnsplashPhotoDataSource,
    private val unsplashDao: UnsplashDao
) {

    // In-memory cache of the photo data
    private var unsplashPhotoDataCache: UnsplashPhotoData? = null

    var latestException: Exception? = null
    private set
    var latestUpdateSource: UpdateSource = UpdateSource.NONE

    // Using a SharedFlow instead of StateFlow as there isn't an initial value to be emitted
    private val dataLastUpdatedFlow = MutableSharedFlow<Long>(replay = 1)
    val dataLastUpDatedObservable: Flow<Long> = dataLastUpdatedFlow

    // Prevents multiple consumers requesting data at the same time
    private val loadPhotoDataLock = Any()

    fun refreshCacheWithRemotePhotoData() {
        val unsplashPhotoData = try {
            remoteDataSource.getRemotePhotoData()
        } catch (e: IOException) {
            latestException = e
            throw e
        }
        if (unsplashPhotoData == null) {
            val e = Exception("Remote returned no photo data")
            latestException = e
            throw e
        }

        // Network data success!
        // Update cache
        synchronized(loadPhotoDataLock) {
            unsplashPhotoDataCache = unsplashPhotoData
            populatePhotoData(unsplashPhotoData)
        }

        // Update meta
        latestException = null
        dataLastUpdatedFlow.tryEmit(System.currentTimeMillis())
        latestUpdateSource = UpdateSource.NETWORK
        latestException = null
    }

    fun getOfflinePhotoData(): UnsplashPhotoData {

    }

    open fun populatePhotoData(photoData: UnsplashPhotoData) {
        val photoEntity = photoData.unsplashPhotos.map { unsplashPhoto ->
            UnsplashEntity(
                id = unsplashPhoto.id,
                urls = unsplashPhoto.urls,
                user = unsplashPhoto.user
            )
        }
        unsplashDao.insertAll(photoEntity)
    }
}