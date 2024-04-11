package io.datalbry.plugin.semver.version

import io.datalbry.plugin.semver.version.model.SemanticVersion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionCalculatorTest {

    private val versionCalculator = VersionCalculator()

    @Test
    fun calculateNextVersion_withoutLastVersion_returnsFirstVersion() {
        val commits = listOf(
            "fix(scope_1): some good fix"
        )
        val nextVersion = versionCalculator.calculateNextVersion(commits, null)

        assertEquals(nextVersion.major, 0)
        assertEquals(nextVersion.minor, 1)
        assertEquals(nextVersion.patch, 0)
    }

    @Test
    fun calculateNextVersion_withBreakingChange_raisesMajor() {
        val commits = listOf(
            "fix(scope_1): BREAKING CHANGE"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 1)
        assertEquals(nextVersion.minor, 0)
        assertEquals(nextVersion.patch, 0)
    }

    @Test
    fun calculateNextVersion_withShortcutBreakingChange_raisesMajor() {
        val commits = listOf(
            "fix(scope_1)!: something"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 1)
        assertEquals(nextVersion.minor, 0)
        assertEquals(nextVersion.patch, 0)
    }

    @Test
    fun calculateNextVersion_withShortcutBreakingChangeAndWithoutScope_raisesMajor() {
        val commits = listOf(
            "fix!: something"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 1)
        assertEquals(nextVersion.minor, 0)
        assertEquals(nextVersion.patch, 0)
    }

    @Test
    fun calculateNextVersion_withFeature_raisesMinor() {
        val commits = listOf(
            "feat(scope_1): best feature"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 0)
        assertEquals(nextVersion.minor, 2)
        assertEquals(nextVersion.patch, 0)
    }

    @Test
    fun calculateNextVersion_withFix_raisesPatch() {
        val commits = listOf(
            "fix(scope_1): ugh, i hate bugs"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 0)
        assertEquals(nextVersion.minor, 1)
        assertEquals(nextVersion.patch, 1)
    }

    @Test
    fun calculateNextVersion_withAllPatchTypes_raisesPatchForEach() {
        val lastVersion = SemanticVersion(1, 0, 0)
        val commitTypes = listOf("fix", "build", "ci", "docs", "pref", "refactor", "style", "test")
        val finalVersion = commitTypes.fold(lastVersion) { currentVersion, nextCommitType ->
            val commits = listOf("$nextCommitType: another one")
            versionCalculator.calculateNextVersion(commits, currentVersion)
        }
        assertEquals(finalVersion.major, 1)
        assertEquals(finalVersion.minor, 0)
        assertEquals(finalVersion.patch, commitTypes.size)
    }

    @Test
    fun calculateNextVersion_withExclamationMark_raisesMajor() {
        val commits = listOf(
            "fix(scope_1)!: ugh, i hate bugs"
        )
        val lastVersion = SemanticVersion(0, 1,0)
        val nextVersion = versionCalculator.calculateNextVersion(commits, lastVersion)

        assertEquals(nextVersion.major, 1)
        assertEquals(nextVersion.minor, 0)
        assertEquals(nextVersion.patch, 0)
    }

}
