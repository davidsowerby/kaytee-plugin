package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * Created by David Sowerby on 15 May 2017
 */
class SimplyCDProjectExtensionTest extends Specification {

    SimplyCDProjectExtension configuration

    def setup() {
        configuration = new SimplyCDProjectExtension()
    }

    def "JSON export"() {
        given:
        ObjectMapper objectMapper = new ObjectMapper()
        StringWriter sw = new StringWriter()
        configuration.remoteRepoUserName = 'freddy'
        configuration.wikiLocalConfiguration.projectName = "who?"
        configuration.gitLocalConfiguration.projectName = "what?"
        configuration.gitRemoteConfiguration.repoUser = "bananarama"
        configuration.baseVersion = "2.3.4.5"

        when:
        objectMapper.writeValue(sw, configuration)
        SimplyCDProjectExtension configuration2 = objectMapper.readValue(sw.toString(), SimplyCDProjectExtension.class)

        then:
        configuration2.remoteRepoUserName == 'freddy'
        configuration2.wikiLocalConfiguration.projectName == "who?"
        configuration2.gitLocalConfiguration.projectName == "what?"
        configuration2.gitRemoteConfiguration.repoUser == "bananarama"
        configuration2.baseVersion == "2.3.4.5"

        configuration == configuration2
    }

    def "copy constructor"() {
        given:
        configuration.remoteRepoUserName = 'freddy'
        configuration.wikiLocalConfiguration.projectName = "who?"
        configuration.gitLocalConfiguration.projectName = "what?"
        configuration.gitRemoteConfiguration.repoUser = "bananarama"
        configuration.baseVersion = "2.3.4.5"

        when:
        SimplyCDProjectExtension configuration2 = new SimplyCDProjectExtension(configuration)

        then:
        configuration2.remoteRepoUserName == 'freddy'
        configuration2.wikiLocalConfiguration.projectName == "who?"
        configuration2.gitLocalConfiguration.projectName == "what?"
        configuration2.gitRemoteConfiguration.repoUser == "bananarama"
        configuration2.baseVersion == "2.3.4.5"

        configuration == configuration2
    }
}
