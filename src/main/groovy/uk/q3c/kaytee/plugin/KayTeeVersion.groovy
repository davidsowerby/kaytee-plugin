package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class KayTeeVersion extends DelegateWithGitPlus {

    private GitSHA currentCommit
    String baseVersion


    KayTeeVersion(Project project) {
        super(project)
    }

    KayTeeVersion(Project project, GitPlus gitPlus) {
        super(project, gitPlus)
    }

    @Override
    String toString() {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        boolean kayTeeConfigured = ext.get(KayTeePlugin.KAYTEE_CONFIG_FLAG)
        if (kayTeeConfigured) {
            prepare()
            baseVersion = config.baseVersion
            logDebug("Base version is $baseVersion")
            String fullVersion = baseVersion + "." + getCurrentCommit().short()
            logDebug("Full version is $fullVersion")
            return fullVersion
        } else {
            project.logger.debug("version not yet available, evaluation not complete")
            return 'version not available until evaluation complete'
        }
    }

    private GitSHA getCurrentCommit() {
        if (currentCommit == null) {
            // this may be called before evaluation complete
            gitPlus.local.projectName = project.name
            gitPlus.local.projectDirParent = project.projectDir.parentFile
            gitPlus.local.prepare(gitPlus.remote) // init checker needs this
            GitBranch currentBranch = gitPlus.local.currentBranch()
            logDebug("Retrieving head commit for version from repo at ${gitPlus.local.projectDir()}, ${currentBranch.name} branch")
            currentCommit = gitPlus.local.headCommitSHA(currentBranch)
        }
        return currentCommit
    }

/**
 * Removes the last element from the version, that is everything from, and including, the last '.'
 * For example 2.2.3.4.55 is returned as 2.2.3.4
 *
 * @param fullVersion
 * @return base version
 */
    static String baseVersionFromFullVersion(String fullVersion) {
        int index = fullVersion.lastIndexOf('.')
        return fullVersion.substring(0, index)

    }

}
