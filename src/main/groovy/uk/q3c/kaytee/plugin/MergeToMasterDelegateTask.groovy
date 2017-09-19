package uk.q3c.kaytee.plugin

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
        gitPlus.propertiesFromGradle()
    }

    void merge() {
        logLifecycle("creating build info file")
        logDebug("Setting up gitPlus from KayTee configuration")
        KayTeeExtension config = project.extensions.getByName("kaytee") as KayTeeExtension
        gitPlus.local.configuration.copyFrom(config.gitLocalConfiguration)
        gitPlus.wikiLocal.configuration.copyFrom(config.wikiLocalConfiguration)
        gitPlus.remote.configuration.copyFrom(config.gitRemoteConfiguration)

        logDebug("Disabling wikiLocal, enabling gitRemote")
        gitPlus.remote.configuration.active(true)
        gitPlus.wikiLocal.configuration.active(false)
        gitPlus.execute()

        GitLocal gitLocal = gitPlus.local
        logDebug("gitPlus set up complete")
        gitLocal.checkoutRemoteBranch(gitLocal.masterBranch())
        gitLocal.mergeBranch(new GitBranch("kaytee"), MergeStrategy.THEIRS, MergeCommand.FastForwardMode.FF)
        gitLocal.push(true, false)
    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg)
    }

    private void logDebug(String msg) {
        project.getLogger().debug(msg)
    }
}
