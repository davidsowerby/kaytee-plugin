package uk.q3c.simplycd.lifecycle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logger
/**
 * Created by David Sowerby on 24 Dec 2016
 */
class AfterEvaluateAction implements Action<Project> {

    final Project project

    AfterEvaluateAction(Project project) {
        this.project = project
    }

    @Override
    void execute(Project project) {
        project.getLogger().debug('after evaluate')
        confirmConfiguration()
    }

    /**
     * Set any required config that has not already been set, or fail
     */
    private void confirmConfiguration() {
        Logger logger = project.getLogger()
        logger.lifecycle("confirming configuration")
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension

        config.wikiLocalConfiguration.active = true
        config.wikiLocalConfiguration.cloneFromRemote = false
        config.wikiLocalConfiguration.projectName = project.name

        config.gitRemoteConfiguration.repoName = project.name
        config.gitRemoteConfiguration.repoUser = config.remoteRepoUserName

        config.gitLocalConfiguration.projectName = project.name
        config.gitLocalConfiguration.projectDirParent = project.projectDir.parentFile
        config.gitLocalConfiguration.cloneFromRemote = false

        config.changeLog.projectName = project.name
        config.changeLog.projectDirParent = project.projectDir.parentFile
        config.changeLog.autoTagLatestCommit = false
        config.changeLog.remoteRepoUser = config.remoteRepoUserName

        logConfig(project)
        config.changeLog.validate()


    }


    private void logConfig(Project project) {
        Logger logger = project.getLogger()
        if (logger.isDebugEnabled()) {
            new ConfigWriter().writeOutConfig(project, "afterEvaluateActionConfig.json", "afterEvaluateActionThresholds.json")
            logger.debug("Configuration settings on completion of AfterEvaluateAction exported to 'afterEvaluateActionConfig.json' and 'afterEvaluateActionThresholds.json'")
        }
    }

}
