package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class KayTeeVersion extends DelegateWithGitPlus {


    KayTeeVersion(Project project) {
        super(project)
    }

    KayTeeVersion(Project project, GitPlus gitPlus) {
        super(project, gitPlus)
    }

    @Override
    String toString() {
        prepare()
        // this may be called before evaluation complete
        gitPlus.local.projectName = project.name
        gitPlus.local.projectDirParent = project.projectDir.parentFile
        gitPlus.local.prepare(gitPlus.remote) // init checker needs this
        GitBranch currentBranch = gitPlus.local.currentBranch()
        logDebug("Base version is ${config.baseVersion}")
        logDebug("Retrieving latest commit for version from repo at ${gitPlus.local.projectDir()}, ${currentBranch.name} branch")
        return config.baseVersion + "." + gitPlus.local.latestCommitSHA(currentBranch).short()
    }
}
