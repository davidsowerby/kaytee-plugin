package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification
import uk.q3c.build.changelog.ChangeLog
import uk.q3c.build.changelog.ChangeLogConfiguration

/**
 * Created by David Sowerby on 29 Dec 2016
 */
class GenerateChangeLogTaskDelegateTest extends Specification {

    Logger logger = Mock(Logger)

    GenerateChangeLogTaskDelegate delegate
    ChangeLog changeLog = Mock(ChangeLog)
    Project project = Mock(Project)
    SimplyCDProjectExtension simplyCdConfig
    ExtensionContainer projectExtensions = Mock(ExtensionContainer)
    ChangeLogConfiguration changeLogConfiguration = Mock()

    void setup() {
        delegate = new GenerateChangeLogTaskDelegate()
        project.logger >> logger
        simplyCdConfig = new SimplyCDProjectExtension()
        project.extensions >> projectExtensions
        projectExtensions.getByName("simplycd") >> simplyCdConfig
    }

    def "Generate"() {
        given:
        changeLog.configuration >> changeLogConfiguration
        simplyCdConfig.remoteRepoUserName = 'munchkin'

        when:
        delegate.generate(project, changeLog)

        then:
        1 * changeLog.configuration.copyFrom(simplyCdConfig.changeLog)

        then:
        1 * changeLog.generate()
    }


}
