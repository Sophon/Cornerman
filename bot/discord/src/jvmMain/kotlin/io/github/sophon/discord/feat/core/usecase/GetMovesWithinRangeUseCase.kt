package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.CoreFilters
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.core.wiki.util.findMatching
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command
import kotlinx.coroutines.flow.first

internal class GetMovesWithinRangeUseCase {
    suspend operator fun invoke(
        wiki: WikiClient,
        command: Command,
        query: String,
    ): Result<Pair<Character, List<Move>>, BotError> {
        val rangeQuery = query.substringAfter(" ", missingDelimiterValue = "")
        val (from, to) = rangeQuery.parseIntoRange()
            ?: return rangeQuery.toFormattedError()

        //TODO: eventually implement OH, OB and CH
        val filter = when (command) {
            Command.Startup -> CoreFilters.Startup(from, to)
            else -> return rangeQuery.toFormattedError()
        }

        val characterQuery = query.substringBefore(" ")
        val characterList = wiki.subscribeToCharacterList().first()
        val character = characterList.findMatching(characterQuery)
            ?: return Result.Error(BotError.UnknownCharacter(characterQuery))

        val moveList = wiki.subscribeToMoveList(CharacterId(character.id)).first()
            .filter(filter.predicate)
            .distinctBy { it.input }

        val result = Result.Success(character to moveList)
        return result
    }


    private fun String.parseIntoRange(): Pair<Int, Int>? {
        val range = this
            .split(" ")
            .mapNotNull {
                when {
                    it.equalsIgnoreCase("inf") || it.equalsIgnoreCase("+inf") -> Int.MAX_VALUE
                    it.equalsIgnoreCase("-inf") -> Int.MIN_VALUE
                    else -> it.toIntOrNull()
                }
            }
            .take(2)

        if (range.size < 2) return null

        val (low, high) = range.sorted()
        return low to high
    }

    private fun String.toFormattedError(): Result.Error<BotError> {
        return Result.Error(BotError.InvalidQuery("SYNTAX: character name followed by two numbers (can be -INF/INF): $this"))
    }
}
