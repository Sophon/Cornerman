package io.github.sophon.fightingnerd.feat.changelog

internal interface ChangelogClient {
    fun hasSeenChangelog(): Boolean
}