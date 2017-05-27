package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.commons.lang.StringUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import uk.q3c.build.gitplus.remote.DefaultGitRemoteUrlMapper
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
        SimplyCDProjectExtension config = confirmConfiguration()
        bintrayConfig(config)
    }

    /**
     * Set any required config that has not already been set, or fail.  See https://github.com/davidsowerby/simplycd-lifecycle/issues/19
     */
    private SimplyCDProjectExtension confirmConfiguration() {
        Logger logger = project.getLogger()
        logger.lifecycle("confirming configuration")
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension

        config.wikiLocalConfiguration.active = true
        config.wikiLocalConfiguration.cloneFromRemote = false
        config.wikiLocalConfiguration.projectName = project.name

        config.gitRemoteConfiguration.repoName = project.name
        config.gitRemoteConfiguration.repoUser = config.remoteRepoUserName

        config.gitLocalConfiguration.cloneFromRemote = false

        config.changeLog.projectName = project.name
        config.changeLog.projectDirParent = project.projectDir.parentFile
        config.changeLog.autoTagLatestCommit = false
        config.changeLog.remoteRepoUser = config.remoteRepoUserName

        logConfig(project)

        config.changeLog.validate()
        return config
    }


    private void logConfig(Project project) {
        Logger logger = project.getLogger()
        if (logger.isDebugEnabled()) {
            new ConfigWriter().writeOutConfig(project, "afterEvaluateActionConfig.json", "afterEvaluateActionThresholds.json")
            logger.debug("Configuration settings on completion of AfterEvaluateAction exported to 'afterEvaluateActionConfig.json' and 'afterEvaluateActionThresholds.json'")
        }
    }

    /**
     * We could make this simpler by declaring these at the start of the {@link SimplyCDPlugin}, but if we did that, the order in which the simplycd and bintray
     * configurations are declared becomes important.  This way, it does not matter
     *
     * @param config
     */
    void bintrayConfig(SimplyCDProjectExtension config) {
        project.logger.debug("Checking existing bintray config values, and replacing nulls where possible")
        DefaultGitRemoteUrlMapper mapper = new DefaultGitRemoteUrlMapper()
        mapper.parent = config.gitRemoteConfiguration
        BintrayExtension bintray = project.extensions.getByName("bintray") as BintrayExtension

        if (StringUtils.isEmpty(bintray.pkg.name)) {
            bintray.pkg.name = config.gitLocalConfiguration.projectName
        }
        if (StringUtils.isEmpty(bintray.pkg.repo)) {
            bintray.pkg.repo = 'maven'
        }

        if (StringUtils.isEmpty(bintray.pkg.websiteUrl)) {
            bintray.pkg.websiteUrl = mapper.repoBaselUrl()
        }
        if (StringUtils.isEmpty(bintray.pkg.issueTrackerUrl)) {
            bintray.pkg.issueTrackerUrl = mapper.issuesUrl()
        }
        if (StringUtils.isEmpty(bintray.pkg.vcsUrl)) {
            bintray.pkg.vcsUrl = mapper.cloneUrl()
        }
        if (StringUtils.isEmpty(bintray.key)) {
            bintray.key = project.bintrayKey
        }

        bintray.pkg.version.name = project.version.toString()

        if (StringUtils.isEmpty(bintray.pkg.version.released)) {
            bintray.pkg.version.released = new Date()
        }


        if (project.logger.isDebugEnabled()) {
            ObjectMapper objectMapper = new ObjectMapper()
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
            StringWriter sw = new StringWriter()
            BintrayConfigWrapper wrapper = new BintrayConfigWrapper(bintray)
            objectMapper.writeValue(sw, wrapper)
            project.logger.debug("Bintray config is:\n")
            project.logger.debug(sw.toString())


            project.logger.debug("project version is: " + project.version.toString())
        }
    }

}
