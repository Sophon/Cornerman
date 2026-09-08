package io.github.sophon.fightingnerd.core.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.core.data.ChangelogRepo
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.coroutines.flow.Flow

internal class ChangelogRepoImpl(
    private val store: DataStore<Preferences>,
    //TODO: make network call
): ChangelogRepo {
    override fun writeLastSeenVersion(version: String): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override fun getLastSeenVersion(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun getReleasedVersions(max: Int): Flow<List<Release>> {
        TODO("Not yet implemented")
    }
}
