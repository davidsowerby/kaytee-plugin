package uk.q3c.simplycd.lifecycle

import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.merge.MergeStrategy
import org.gradle.api.Project
import uk.q3c.build.gitplus.GitPlusFactory
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal

/**
 * Created by David Sowerby on 20 May 2017
 */
class MergeToMasterDelegateTask {

    final Project project
    GitPlus gitPlus

    MergeToMasterDelegateTask(Project project) {
        this.project = project
        gitPlus = GitPlusFactory.instance
    }

    void merge() {
        logLifecycle("creating build info file")
        logDebug("Setting up gitPlus from SimplyCD configuration")
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        gitPlus.local.localConfiguration.copyFrom(config.gitLocalConfiguration)
        gitPlus.wikiLocal.localConfiguration.copyFrom(config.wikiLocalConfiguration)
        gitPlus.remote.configuration.copyFrom(config.gitRemoteConfiguration)

        logDebug("Disabling wikiLocal, enabling gitRemote")
        gitPlus.remote.getConfiguration().active(true)
        gitPlus.wikiLocal.localConfiguration.active(false)
        gitPlus.execute()

        GitLocal gitLocal = gitPlus.local
        logDebug("gitPlus set up complete")
        gitLocal.checkoutBranch(gitLocal.masterBranch())
        gitLocal.mergeBranch(new GitBranch("simplycd"), MergeStrategy.THEIRS, MergeCommand.FastForwardMode.FF)
        gitLocal.push(true, false)
    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg)
    }

    private void logDebug(String msg) {
        project.getLogger().debug(msg)
    }
}
