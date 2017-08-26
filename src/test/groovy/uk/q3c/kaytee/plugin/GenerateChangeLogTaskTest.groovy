package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import uk.q3c.build.changelog.ChangeLog
import uk.q3c.util.version.VersionNumberKt

/**
 * Created by David Sowerby on 30 Dec 2016
 */
class GenerateChangeLogTaskTest extends Specification {


    GenerateChangeLogTask task

    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        project.version = VersionNumberKt.parseFullVersionNumber('9.9.9.1000')
        task = project.getTasks().create("generateChangeLog", GenerateChangeLogTask.class)
    }

    def "invoke delegate"() {
        given:
        GenerateChangeLogTaskDelegate delegate = Mock(GenerateChangeLogTaskDelegate)
        task.delegate = delegate

        when:
        task.generate()

        then:
        1 * delegate.generate(project, _ as ChangeLog)
    }
}
