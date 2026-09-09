package io.github.sophon.fightingnerd.feat.more.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.ic_discord
import fightingnerd.composeapp.generated.resources.ic_fighting_nerd
import fightingnerd.composeapp.generated.resources.more_about_about_body
import fightingnerd.composeapp.generated.resources.more_about_about_title
import fightingnerd.composeapp.generated.resources.more_about_invite_discord
import fightingnerd.composeapp.generated.resources.more_about_name_body
import fightingnerd.composeapp.generated.resources.more_about_name_title
import fightingnerd.composeapp.generated.resources.more_about_next_body
import fightingnerd.composeapp.generated.resources.more_about_next_title
import io.github.sophon.fightingnerd.core.ui.components.TopBarButton
import io.github.sophon.fightingnerd.feat.more.URL_DISCORD_INVITE
import io.github.sophon.fightingnerd.theme.FightingNerdTheme
import io.github.sophon.fightingnerd.theme.nerdColorPalette
import io.github.sophon.fightingnerd.theme.nerdDimensions
import io.github.sophon.fightingnerd.theme.nerdTypography
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun AboutScreen(
    onExit: () -> Unit,
    onDiscordClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(nerdColorPalette.background)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = nerdDimensions.screenPaddingHorizontal,
                vertical = nerdDimensions.screenPaddingVertical,
            ),
    ) {
        Header(onExit)
        Spacer(Modifier.height(nerdDimensions.componentGap))

        Section(
            title = stringResource(Res.string.more_about_about_title),
            body = stringResource(Res.string.more_about_about_body)
        )
        Spacer(Modifier.height(nerdDimensions.componentGap))

        Section(
            title = stringResource(Res.string.more_about_name_title),
            body = stringResource(Res.string.more_about_name_body),
        )
        Spacer(Modifier.height(nerdDimensions.componentGap))

        Section(
            title = stringResource(Res.string.more_about_next_title),
            body = stringResource(Res.string.more_about_next_body),
        )
        Spacer(Modifier.height(nerdDimensions.componentGap))

        DiscordInvite(onClick = onDiscordClick)
        Spacer(Modifier.height(nerdDimensions.componentGap))
    }
}

@Composable
private fun Header(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        TopBarButton(
            onClick = onExit,
            modifier = Modifier.align(Alignment.TopStart),
        )
        Image(
            painter = painterResource(Res.drawable.ic_fighting_nerd),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(nerdDimensions.iconHeadline),
        )
    }
}

@Composable
private fun Section(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title.uppercase(),
            style = nerdTypography.headlineSmall,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Text(
            text = body,
            style = nerdTypography.bodyLarge,
            color = nerdColorPalette.textPrimary,
        )
    }
}

@Composable
private fun DiscordInvite(
    onClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.more_about_invite_discord).uppercase(),
            style = nerdTypography.headlineSmall,
            color = nerdColorPalette.textPrimary,
        )
        Spacer(Modifier.height(nerdDimensions.componentGapTight))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(nerdColorPalette.surfaceHigh)
                .clickable(onClick = { onClick(URL_DISCORD_INVITE) })
                .padding(nerdDimensions.componentPadding),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_discord),
                contentDescription = null,
                tint = nerdColorPalette.accent,
                modifier = Modifier.size(nerdDimensions.iconLarge),
            )
        }

        Text(
            text = "Add to server",
            style = nerdTypography.labelLarge,
            color = nerdColorPalette.textSecondary,
        )
    }
}


//region PREVIEW
@Composable
@Preview()
private fun Preview() {
    FightingNerdTheme {
        AboutScreen(
            onExit = {},
            onDiscordClick = {},
        )
    }
}
//endregion
