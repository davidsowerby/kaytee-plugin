package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.commons.lang.StringUtils
import org.gradle.api.Project
import uk.q3c.build.gitplus.remote.DefaultGitRemoteUrlMapper

import java.time.LocalDateTime

/**
 * Created by David Sowerby on 20 May 2017
 */
class PrepareBintrayDelegateTask extends DelegateWithConfig {


    PrepareBintrayDelegateTask(Project project) {
        super(project)
    }

    void prepare() {
        DefaultGitRemoteUrlMapper mapper = new DefaultGitRemoteUrlMapper()
        mapper.parent = config.gitRemoteConfiguration
        BintrayExtension bintray = project.extensions.getByName("bintray") as BintrayExtension

        if (bintray.publications.length == null || bintray.publications.length == 0) {
            bintray.publications = ['mavenStuff']
        }
        if (StringUtils.isEmpty(bintray.pkg.name)) {
            bintray.pkg.name = config.gitLocalConfiguration.projectName
        }
        if (StringUtils.isEmpty(bintray.pkg.websiteUrl)) {
            bintray.pkg.websiteUrl = mapper.repoBaselUrl()
        }
        if (StringUtils.isEmpty(bintray.pkg.issueTrackerUrl)) {
            bintray.pkg.issueTrackerUrl = mapper.issuesUrl()
        }
        if (StringUtils.isEmpty(bintray.pkg.issueTrackerUrl)) {
            bintray.pkg.vcsUrl = mapper.cloneUrl()
        }
        if (StringUtils.isEmpty(bintray.key)) {
            bintray.key = project.bintrayKey
        }
        if (StringUtils.isEmpty(bintray.pkg.version.name)) {
            bintray.pkg.version.name = project.version.toString()
        }
        if (StringUtils.isEmpty(bintray.pkg.version.released)) {
            bintray.pkg.version.released = LocalDateTime.now().toString()
        }

        if (project.logger.isDebugEnabled()) {
            ObjectMapper objectMapper = new ObjectMapper()
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
            StringWriter sw = new StringWriter()
            objectMapper.writeValue(sw, bintray)
            project.logger.debug("Bintray config is:\n")
            project.logger.debug(sw.toString())
        }
    }

    void logDebug(String msg) {
        project.getLogger().debug(msg)
    }
}
