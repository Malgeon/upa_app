package com.example.upa_app.data.unsplash

import androidx.annotation.WorkerThread
import com.example.upa_app.data.UpdateSource
import com.example.upa_app.data.db.*
import com.example.upa_app.model.ConferenceData
import com.example.upa_app.model.ConferenceDay
import com.example.upa_app.model.unsplash.UnsplashPhotoData
import com.example.upa_app.shared.result.Result
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

    @WorkerThread
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
        synchronized(loadPhotoDataLock) {
            val offlineData = unsplashPhotoDataCache ?: getCacheOrLocalDataAndPopulatePhoto()
            unsplashPhotoDataCache = offlineData
            return offlineData
        }
    }

    private fun getCacheOrLocalDataAndPopulatePhoto(): UnsplashPhotoData {
        val photoData = getCacheOrLocalData()

    }

    private fun getCacheOrLocalData(): UnsplashPhotoData {
        // First, try the local cache:
        var photoData =
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

    @WorkerThread
    fun refreshCacheWithRemotePhotoDataFlow(): Flow<Result<T>> =
        flow {
            val unsplashPhotoData = try {
                remoteDataSource.getRemotePhotoData()
            } catch (e: IOException) {
                emit(Result.Error(e))
            }
            if (unsplashPhotoData == null) {
                val e = Exception("Remote returned no photo data")
                latestException = e
                emit(Result.Error(e))
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


}

class SampleUnsplashPhotoRepository {


    // In-memory cache of the conference data
    private var conferenceDataCache: ConferenceData? = null

    val currentConferenceDataVersion: Int
        get() = conferenceDataCache?.version ?: 0

    var latestException: Exception? = null
        private set
    var latestUpdateSource: UpdateSource = UpdateSource.NONE
        private set

    // Using a SharedFlow instead of StateFlow as there isn't an initial value to be emitted
    private val dataLastUpdatedFlow = MutableSharedFlow<Long>(replay = 1)
    val dataLastUpDatedObservable: Flow<Long> = dataLastUpdatedFlow

    // Prevents multiple consumers requesting data at the same time
    private val loadConfDataLock = Any()

    fun refreshCacheWithRemoteConferenceData() {
        val conferenceData = try {
            remoteDataSource.getRemoteConferenceData()
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

    fun getOfflineConferenceData(): ConferenceData {
        synchronized(loadConfDataLock) {
            val offlineData = conferenceDataCache ?: getCacheOrBootstrapDataAndPopulateSearch()
            conferenceDataCache = offlineData
            return offlineData
        }
    }

    private fun getCacheOrBootstrapDataAndPopulateSearch(): ConferenceData {
        val conferenceData = getCacheOrBootstrapData()
        populateSearchData(conferenceData)
        return conferenceData
    }

    private fun getCacheOrBootstrapData(): ConferenceData {
        // First, try the local cache:
        var conferenceData = remoteDataSource.getOfflineConferenceData()

        // Cache success!
        if (conferenceData != null) {
            latestUpdateSource = UpdateSource.CACHE
            return conferenceData
        }

        // Second, use the bootstrap file:
        conferenceData = boostrapDataSource.getOfflineConferenceData()!!
        latestUpdateSource = UpdateSource.BOOTSTRAP
        return conferenceData
    }

    open fun populateSearchData(conferenceData: ConferenceData) {
        Timber.e("data populate")
        val sessionFtsEntities = conferenceData.sessions.map { session ->
            SessionFtsEntity(
                sessionId = session.id,
                title = session.title,
                description = session.description,
                speakers = session.speakers.joinToString { it.name }
            )
        }
        appDatabase.sessionFtsDao().insertAll(sessionFtsEntities)
        val speakers = conferenceData.speakers.map {
            SpeakerFtsEntity(
                speakerId = it.id,
                name = it.name,
                description = it.biography
            )
        }
        appDatabase.speakerFtsDao().insertAll(speakers)
        val codelabs = conferenceData.codelabs.map {
            CodelabFtsEntity(
                codelabId = it.id,
                title = it.title,
                description = it.description
            )
        }
        appDatabase.codelabFtsDao().insertAll(codelabs)
    }

    open fun getConferenceDays(): List<ConferenceDay> = TimeUtils.ConferenceDays
}
