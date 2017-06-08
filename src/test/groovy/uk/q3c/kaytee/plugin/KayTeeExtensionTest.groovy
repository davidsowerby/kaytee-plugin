package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import static uk.q3c.kaytee.plugin.TaskKey.Integration_Test

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
        configuration.release.toBintray = false
        configuration.unitTest.enabled = false

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
        configuration.integrationTest == configuration2.testConfig(Integration_Test)
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
        configuration.release.mergeToMaster = false
        configuration.unitTest.enabled = false


        when:
        KayTeeExtension configuration2 = new KayTeeExtension(configuration)

        then:
        configuration2.remoteRepoUserName == 'freddy'
        configuration2.wikiLocalConfiguration.projectName == "who?"
        configuration2.gitLocalConfiguration.projectName == "what?"
        configuration2.gitRemoteConfiguration.repoUser == "bananarama"
        configuration2.baseVersion == "2.3.4.5"
        !configuration2.release.mergeToMaster

        configuration == configuration2
        configuration.integrationTest == configuration2.testConfig(Integration_Test)
    }

    def "test config for all groups, only unit test enabled by default"() {

        expect:
        configuration.testConfig(TaskKey.Unit_Test).enabled
        !configuration.testConfig(TaskKey.Acceptance_Test).enabled
        !configuration.testConfig(TaskKey.Functional_Test).enabled
        !configuration.testConfig(Integration_Test).enabled
        !configuration.testConfig(TaskKey.Production_Test).enabled
    }

    def "testConfig"() {

        when:
        configuration.testConfig(Integration_Test).thresholds.method = 33
        configuration.testConfig(Integration_Test) == configuration.integrationTest

        then:
        configuration.testConfig(Integration_Test).thresholds.method == 33

        when:
        configuration.testConfig(TaskKey.Custom)

        then:
        thrown IllegalArgumentException
    }

}
