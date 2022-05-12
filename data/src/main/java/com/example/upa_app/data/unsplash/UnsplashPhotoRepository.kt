package com.example.upa_app.data.unsplash

import com.example.upa_app.data.UpdateSource
import com.example.upa_app.data.db.*
import com.example.upa_app.model.ConferenceDay
import com.example.upa_app.model.unsplash.UnsplashPhotoData
import com.example.upa_app.model.unsplash.UnsplashPhotoUrls
import com.example.upa_app.model.unsplash.UnsplashUser
import com.example.upa_app.shared.util.TimeUtils
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoRepository @Inject constructor(
    private val remoteDataSource: UnsplashPhotoDataSource,
    private val unsplashDao: UnsplashDao
) {
    // In-memory cache of the conference data
    private var conferenceDataCache: UnsplashPhotoData? = null

    var latestException: Exception? = null
        private set
    var latestUpdateSource: UpdateSource = UpdateSource.NONE
        private set

    // Using a SharedFlow instead of StateFlow as there isn't an initial value to be emitted
    private val dataLastUpdatedFlow = MutableSharedFlow<Long>(replay = 1)
    val dataLastUpDatedObservable: Flow<Long> = dataLastUpdatedFlow

    // Prevents multiple consumers requesting data at the same time
    private val loadConfDataLock = Any()

    suspend fun refreshCacheWithRemoteConferenceData() {
        val conferenceData = try {
            remoteDataSource.getRemotePhotoData()
        } catch (e: IOException) {
            latestException = e
            throw e
        }
        if (conferenceData == null) {
            val e = Exception("Remote returned no conference data")
            latestException = e
            throw e
        }

        // Network data success!
        // Update cache
        synchronized(loadConfDataLock) {
            conferenceDataCache = conferenceData
            populateSearchData(conferenceData)
        }

        // Update meta
        latestException = null
        dataLastUpdatedFlow.tryEmit(System.currentTimeMillis())
        latestUpdateSource = UpdateSource.NETWORK
        latestException = null
    }

    fun getOfflineConferenceData(): UnsplashPhotoData {
        synchronized(loadConfDataLock) {
            val offlineData = conferenceDataCache ?: getCacheOrLocalDataAndPopulateSearch()
            conferenceDataCache = offlineData
            return offlineData
        }
    }

    private fun getCacheOrLocalDataAndPopulateSearch(): UnsplashPhotoData {
        val conferenceData = getCacheOrLocalData()
        populateSearchData(conferenceData)
        return conferenceData
    }

    private fun getCacheOrLocalData(): UnsplashPhotoData {
        // First, try the local cache:
        var conferenceData = unsplashDao.searchAll()

        // Cache success!
        if (conferenceData != null) {
            latestUpdateSource = UpdateSource.CACHE
            return conferenceData
        }

        // Second, use the local data:
        conferenceData = unsplashDao.searchAll()
        latestUpdateSource = UpdateSource.LOCAL
        return conferenceData
    }

    open fun populateSearchData(photoData: UnsplashPhotoData) {
        Timber.e("data populate")
        val unsplashEntity = photoData.unsplashPhotos.map { photo ->
            UnsplashEntity(
                id = photo.id,
                urls = photo.urls,
                user = photo.user
            )
        }
        unsplashDao.insertAll(unsplashEntity)
    }

    open fun getConferenceDays(): List<ConferenceDay> = TimeUtils.ConferenceDays
}
