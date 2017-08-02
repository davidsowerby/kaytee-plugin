package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitLocalException
import uk.q3c.build.gitplus.local.Tag
import uk.q3c.util.version.Scheme
import uk.q3c.util.version.VersionNumber
import uk.q3c.util.version.VersionNumberKt
/**
 * Created by David Sowerby on 13 Jun 2017
 */
class VersionCheckTaskDelegate extends DelegateWithGitPlus {


    VersionCheckTaskDelegate(Project project) {
        super(project)
    }

    VersionCheckTaskDelegate(Project project, GitPlus gitPlus) {
        super(project, gitPlus)
    }

    /**
     *
     * Constructs the {@link VersionNumber} object from configuration data, and sets project.version with it
     *
     * Throws GitLocalException if the proposed version is found in another tag (unless that tag is attached to the
     * current HEAD, in which case it is ignored, and true is returned)
     *
     * @return true if the version has been used before but ONLY on the HEAD commit - in other words, this commit has already been tagged with this version,
     * a situation that is valid for a build re-run.
     *
     * @throws GitLocalException if the baseVersion is in a tag pointing to a different commit, that is, an attempt is being made to re-use a version
     */
    boolean check() {
        prepare()
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        String currentCommitId = ext.get(KayTeePlugin.KAYTEE_COMMIT_ID)
        String version = config.version.number
        String qualifier = config.version.qualifier
        String buildMetaData = config.version.buildMetaData
        Scheme scheme = config.version.scheme

        // We want to check for existing version without build meta, but we can use the VersionNumber compare methods to do that
        VersionNumber newVersion = VersionNumberKt.parseVersion(version,qualifier,buildMetaData,scheme)
        project.version=newVersion
        logLifecycle("checking version of '$newVersion' to ensure it has not been used before")

        // look for existing tag with this version
        final List<Tag> tags = gitPlus.local.tags()
        logDebug("Number of existing tags: ${tags.size()}")
        def newVersionStr = newVersion.toString()
        for (Tag tag : tags) {
            logDebug("checking tag with name: ${tag.tagName}")
            VersionNumber tagVersion=VersionNumberKt.parseFullVersionNumber(tag.tagName,scheme)
            if (tagVersion.isSameVersionAs(newVersion)) {
                logDebug("version tag already exists")
                if (tag.commit.hash != currentCommitId) {
                    throw new GitLocalException("A duplicate tag ${tagVersion.toString()} has been found, but is attached to commit ${tag.commit.hash}, instead of the current commit ${currentCommitId}")
                } else {
                    logLifecycle("Existing tag ${tag.tagName} is valid, version check complete")
                    ext.set(KayTeePlugin.KAYTEE_RERUN, true)
                    return true
                }
            }
        }
        ext.set(KayTeePlugin.KAYTEE_RERUN, false)
        logLifecycle("Version check complete, using new version '$newVersionStr'")
        return false
    }

}
