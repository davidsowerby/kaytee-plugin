package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 30 Dec 2016
 */
class GenerateChangeLogTaskTest extends Specification {

    SimplyCDVersion versionObject = Mock(SimplyCDVersion)

    GenerateChangeLogTask task

    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
        versionObject.toString() >> '9.9.9.1000'
        project.version = versionObject
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
