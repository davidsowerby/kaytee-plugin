package uk.q3c.kaytee.plugin

import net.sf.json.test.JSONAssert
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.util.testutil.TestResource

import static org.mockito.Mockito.*

/**
 * Created by David Sowerby on 08 Sep 2017
 */
class ConfigWriterTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    ConfigWriter writer

    File temp
    File buildDir
    Project project = mock(Project)
    ExtensionContainer extensions = mock(ExtensionContainer)
    KayTeeExtension kayteeExtension
    Logger logger = mock(Logger)

    def setup() {
        temp = temporaryFolder.getRoot()
        buildDir = new File(temp, "buildDir")
        writer = new ConfigWriter()
        kayteeExtension = new KayTeeExtension()

        when(project.getExtensions()).thenReturn(extensions)
        when(project.buildDir).thenReturn(buildDir)
        when(project.logger).thenReturn(logger)
        when(extensions.getByName('kaytee')).thenReturn(kayteeExtension)
    }

    def "write out"() {
        given:
        File expectedFile = TestResource.resource(this, "kaytee.json")
        String expected = FileUtils.readFileToString(expectedFile)

        when:

        File actualFile = writer.writeOutConfig(project, "kaytee.json")
        String actual = FileUtils.readFileToString(actualFile)

        then:
        JSONAssert.assertEquals(expected, actual)
    }

//    def "other inputs"() {
//        given:
//        Gson gson = new GsonBuilder().setExclusionStrategies(new JacksonAnnotationExclusionStrategy()).create();
//
//        when:
//        String output = gson.toJson(jsonConfig)
//        FileUtils.writeStringToFile(ktFile,output)
//
//        then:
//    }
}
