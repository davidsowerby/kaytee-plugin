package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.internal.reflect.Instantiator
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.util.testutil.FileTestUtil
import uk.q3c.util.testutil.TestResource

/**
 * Created by David Sowerby on 19 Jan 2017
 */
class ConfigToJsonTaskDelegateTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    Logger logger = Mock(Logger)
    ConfigToJsonTaskDelegate delegate
    Project project = Mock(Project)
    ExtensionContainer extensionContainer = Mock(ExtensionContainer)
    SimplyCDProjectExtension projectInfo = new SimplyCDProjectExtension()
    ThresholdsContainer thresholdsContainer = new ThresholdsContainer(instantiator)
    ObjectMapper objectMapper = new ObjectMapper()
    Instantiator instantiator = Mock()

    void setup() {
        thresholdsContainer.add(new TestGroupThresholds('integrationTest'))
        temp = temporaryFolder.getRoot()
        project.buildDir >> new File(temp, 'build')
        project.logger >> logger
        project.extensions >> extensionContainer
        extensionContainer.getByName('simplycd') >> projectInfo
        extensionContainer.getByName('thresholds') >> thresholdsContainer
        delegate = new ConfigToJsonTaskDelegate(project)
    }

    def "writeInfo"() {
        given:
        File expectedSimplyFile = new File(temp, 'build/simplycd.json')
        File expectedThresholdsFile = new File(temp, 'build/thresholds.json')

        when:
        delegate.writeInfo()

        then:
        expectedSimplyFile.exists()
        expectedThresholdsFile.exists()

        when: "run a second time, so tha t build dir already there"
        delegate.writeInfo()

        then:
        expectedSimplyFile.exists()
        expectedThresholdsFile.exists()

        then:
        !FileTestUtil.compare(expectedSimplyFile, TestResource.resource(this, 'simplycd.json')).present
        !FileTestUtil.compare(expectedThresholdsFile, TestResource.resource(this, 'thresholds.json')).present

    }


}
