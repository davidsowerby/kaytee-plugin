package uk.q3c.kaytee.plugin

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
    KayTeeExtension ktConfig
    ExtensionContainer projectExtensions = Mock(ExtensionContainer)
    ChangeLogConfiguration changeLogConfiguration = Mock(ChangeLogConfiguration)

    void setup() {
        delegate = new GenerateChangeLogTaskDelegate()
        project.logger >> logger
        ktConfig = new KayTeeExtension()
        project.extensions >> projectExtensions
        projectExtensions.getByName("kaytee") >> ktConfig
    }

    def "Generate"() {
        given:
        changeLog.configuration >> changeLogConfiguration
        ktConfig.remoteRepoUserName = 'munchkin'

        when:
        delegate.generate(project, changeLog)

        then:
        1 * changeLog.configuration.copyFrom(ktConfig.changeLog)

        then:
        1 * changeLog.generate()
    }


}
