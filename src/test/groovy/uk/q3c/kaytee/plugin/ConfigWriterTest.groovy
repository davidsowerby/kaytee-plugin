package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

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

    def "round trip"() {
        given:
        kayteeExtension.changelog.useStoredIssues = false // random change to defaults
        ObjectMapper mapper = new ObjectMapper()

        when:
        File actualFile = writer.writeOutConfig(project, "kaytee.json")
        KayTeeExtension actual = mapper.readValue(actualFile, KayTeeExtension.class)

        then:
        kayteeExtension == actual
    }

}
