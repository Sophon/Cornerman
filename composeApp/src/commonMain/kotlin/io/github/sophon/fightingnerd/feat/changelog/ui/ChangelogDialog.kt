package io.github.sophon.fightingnerd.feat.changelog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.sophon.fightingnerd.feat.changelog.model.Release
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ChangelogDialog(
    release: Release,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = { Text(release.version) },
        text = {
            Column {
                release.changeList.forEach { change ->
                    Text("• $change")
                }
            }
        },
        modifier = modifier,
    )
}


//region PREVIEW
@Composable
@Preview()
private fun ChangelogDialogPreview() {
    FightingNerdTheme {
        ChangelogDialog(
            release = Release(
                version = "v4.0.1",
                isPreRelease = false,
                changeList = persistentListOf(
                    "fixed updater on iOS",
                    "improved character search performance",
                    "added Tekken 8 tier list",
                ),
            ),
            onDismiss = {},
        )
    }
}
//endregion
