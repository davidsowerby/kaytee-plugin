package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * Created by David Sowerby on 15 May 2017
 */
class KayTeeExtensionTest extends Specification {

    KayTeeExtension configuration

    def setup() {
        configuration = new KayTeeExtension()
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
        configuration.generateBuildInfo = false
        configuration.generateChangeLog = false
        configuration.publishToBintray = false

        when:
        objectMapper.writeValue(sw, configuration)
        KayTeeExtension configuration2 = objectMapper.readValue(sw.toString(), KayTeeExtension.class)

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
        configuration.generateBuildInfo = false
        configuration.generateChangeLog = false
        configuration.publishToBintray = false


        when:
        KayTeeExtension configuration2 = new KayTeeExtension(configuration)

        then:
        configuration2.remoteRepoUserName == 'freddy'
        configuration2.wikiLocalConfiguration.projectName == "who?"
        configuration2.gitLocalConfiguration.projectName == "what?"
        configuration2.gitRemoteConfiguration.repoUser == "bananarama"
        configuration2.baseVersion == "2.3.4.5"

        configuration == configuration2
    }

    def "testConfig"() {

        when:
        configuration.testConfig(TaskNames.INTEGRATION_TEST).thresholds.method = 33

        then:
        configuration.testConfig(TaskNames.INTEGRATION_TEST).thresholds.method == 33

        when:
        configuration.testConfig("rubbish")

        then:
        thrown IllegalArgumentException
    }
}
