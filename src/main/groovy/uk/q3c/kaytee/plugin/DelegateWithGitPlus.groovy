package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.gradle.api.Project
import uk.q3c.build.gitplus.GitPlusFactory
import uk.q3c.build.gitplus.gitplus.GitPlus

/**
 * Created by David Sowerby on 24 May 2017
 */
class DelegateWithGitPlus extends DelegateWithConfig {

    GitPlus gitPlus


    DelegateWithGitPlus(Project project) {
        super(project)
        gitPlus = GitPlusFactory.instance

    }

    DelegateWithGitPlus(Project project, GitPlus gitPlus) {
        super(project)
        this.gitPlus = gitPlus

    }

    @Override
    protected void prepare() {
        super.prepare()
        project.logger.debug("configuring GitPlus for ${this.getClass().getName()}")
        gitPlus.local.localConfiguration.copyFrom(config.gitLocalConfiguration)
        gitPlus.wikiLocal.localConfiguration.copyFrom(config.wikiLocalConfiguration)
        gitPlus.remote.configuration.copyFrom(config.gitRemoteConfiguration)
        ObjectMapper mapper = getObjectMapper()
        StringWriter sw = new StringWriter()
        mapper.writeValue(sw, gitPlus.remote.configuration)
        project.logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>\n" + sw.toString())
        gitPlus.execute()

    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        return objectMapper
    }
}
