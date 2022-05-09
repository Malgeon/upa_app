package com.example.upa_app.data.unsplash

class UnsplashPhotoDefaultRepository constructor(
    private val conferenceApi: ConferenceApi,
    private val githubApi: GithubApi,
    private val localCacheProvider: LocalCacheProvider
) {

    override suspend fun getEventHistory(): List<Event> {
        return runCatching {
            conferenceApi.getEventHistory()
        }.getOrDefault(localCacheProvider.getEventHistory())
    }

    override suspend fun getSessions(): List<SessionData> {
        return runCatching {
            conferenceApi.getSessions()
        }.getOrDefault(localCacheProvider.getSessions())
    }

    override suspend fun getSponsors(): List<Sponsor> {
        return runCatching {
            conferenceApi.getSponsors()
        }.getOrDefault(localCacheProvider.getSponsors())
    }

    override suspend fun getStaff(): List<User> {
        return runCatching {
            conferenceApi.getStaff()
        }.getOrDefault(localCacheProvider.getStaff())
    }

    override suspend fun getContributors(
        owner: String,
        name: String,
        pageNo: Int
    ): List<User> {
        return githubApi.getContributors(owner, name, pageNo)
            .map {
                User(
                    name = it.name,
                    photoUrl = it.photoUrl
                )
            }
    }
}
