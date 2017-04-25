package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.internal.reflect.Instantiator
import org.junit.Rule
import org.junit.rules.TemporaryFolder
/**
 * Created by David Sowerby on 19 Jan 2017
 */
class ConfigToJsonTaskDelegateTest extends JsonTest {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    Logger logger = Mock(Logger)
    ConfigToJsonTaskDelegate delegate
    Project project = Mock(Project)
    ExtensionContainer extensionContainer = Mock(ExtensionContainer)
    SimplyCDProjectExtension simplyConfig = new SimplyCDProjectExtension()
    ThresholdsContainer thresholdsContainer = new ThresholdsContainer(instantiator)
    ObjectMapper objectMapper = new ObjectMapper()
    Instantiator instantiator = Mock(Instantiator)

    def setup() {
        thresholdsContainer.add(new TestGroupThresholds('integrationTest'))
        thresholdsContainer.add(new TestGroupThresholds('acceptanceTest'))
        temp = temporaryFolder.getRoot()
        project.buildDir >> new File(temp, 'build')
        project.logger >> logger
        project.extensions >> extensionContainer
        extensionContainer.getByName('simplycd') >> simplyConfig
        extensionContainer.getByName('thresholds') >> thresholdsContainer
        delegate = new ConfigToJsonTaskDelegate(project)
    }

    def "write default info"() {
        given:
        File actualSimplyFile = new File(temp, 'build/simplycd.json')
        File actualThresholdsFile = new File(temp, 'build/thresholds.json')
        resource = thresholdsContainer

        when:
        delegate.writeInfo()

        then:
        actualSimplyFile.exists()
        actualThresholdsFile.exists()

        when: "run a second time, so that build dir already there"
        delegate.writeInfo()

        then:
        actualSimplyFile.exists()
        actualThresholdsFile.exists()

        then:
        asExpectedFromFile("thresholds.json")

        when:
        resource = simplyConfig

        then:
        asExpectedFromFile("simplycd.json")
    }

    def "write modified lifecycle info"() {
        given:
        File actualSimplyFile = new File(temp, 'build/simplycd.json')
        File actualThresholdsFile = new File(temp, 'build/thresholds.json')

        simplyConfig.integrationTest.enabled = true
        simplyConfig.integrationTest.qualityGate = true
        simplyConfig.acceptanceTest.enabled = true
        simplyConfig.acceptanceTest.auto = false
        simplyConfig.acceptanceTest.manual = true
        simplyConfig.acceptanceTest.external = false
        simplyConfig.acceptanceTest.external = true
        simplyConfig.acceptanceTest.externalRepoUrl = "https://example.com"
        simplyConfig.acceptanceTest.externalRepoTask = "acceptance-test"
        resource = simplyConfig

        when:
        delegate.writeInfo()
        resource2 = objectMapper.readValue(actualSimplyFile, SimplyCDProjectExtension)


        then:
        resource == resource2
    }

    def "write modified threshold info"() {
        given:
        File actualThresholdsFile = new File(temp, 'build/thresholds.json')
        TestGroupThresholds acceptanceLevels = thresholdsContainer.getByName("acceptanceTest")
        acceptanceLevels.instruction = 81.1
        acceptanceLevels.branch = 70.1
        acceptanceLevels.line = 90.1

        resource = thresholdsContainer

        when:
        delegate.writeInfo()


        then:
        asExpectedFromFile("thresholds2.json")
    }

}
