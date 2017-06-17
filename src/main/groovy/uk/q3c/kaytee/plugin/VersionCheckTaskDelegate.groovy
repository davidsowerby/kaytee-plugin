package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocalException
import uk.q3c.build.gitplus.local.Tag
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
     * Throws GitLocalException if the proposed baseVersion is found in another tag (unless that tag is attached to the
     * current HEAD, in which case it is ignored, anf true is returned)
     *
     * @return true if the version has been used before but ONLY on the HEAD commit - in other words, this commit has already been tagged with this version,
     * a situation that is valid for a build re-run.  This is used by the CreateBuildInfoTaskDelegate, which actually applies the new tag
     *
     * @throws GitLocalException if the baseVersion is in a tag pointing to a different commit
     */
    boolean check() {
        prepare()
        String baseVersion = config.baseVersion
        logLifecycle("checking base version of '$baseVersion' to ensure it has not been used before")

        // look for existing tag with this version
        final List<Tag> tags = gitPlus.local.tags()
        logDebug("Number of existing tags: ${tags.size()}")
        boolean tagFound = false
        def fullVersion = new KayTeeVersion(project).toString()
        GitBranch currentBranch = gitPlus.local.currentBranch()
        GitSHA currentSha = gitPlus.local.headCommitSHA(currentBranch)
        String currentHash = currentSha.sha
        for (Tag tag : tags) {
            logDebug("checking tag with name: ${tag.tagName}")
            if (KayTeeVersion.baseVersionFromFullVersion(tag.tagName).equals(baseVersion)) {
                logDebug("version tag already exists")
                if (tag.commit.hash != currentHash) {
                    throw new GitLocalException("A duplicate tag '$baseVersion' has been found, but is attached to commit ${tag.commit.hash}, instead of the current commit ${currentHash}")
                } else {
                    logLifecycle("Existing tag ${tag.tagName} is valid, version check complete")
                    ext.set(KayTeePlugin.KAYTEE_RERUN, true)
                    return true
                }
            }
        }
        logLifecycle("Version check complete, using new version '$fullVersion'")
        return false
    }

}
