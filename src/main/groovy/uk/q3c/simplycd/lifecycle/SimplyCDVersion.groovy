package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class SimplyCDVersion extends DelegateWithGitPlus {

    SimplyCDVersion(Project project) {
        super(project)
    }

    SimplyCDVersion(Project project, GitPlus gitPlus) {
        super(project, gitPlus)
    }

    @Override
    String toString() {
        prepare()
        GitBranch currentBranch = gitPlus.local.currentBranch()
        return config.baseVersion + "." + gitPlus.local.latestCommitSHA(currentBranch).short()
    }
}
