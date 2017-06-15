package uk.q3c.kaytee.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
/**
 * Created by David Sowerby on 06 Dec 2016
 */
class CreateBuildInfoTaskTest extends Specification {


    CreateBuildInfoTask task
    CreateBuildInfoTaskDelegate delegate = Mock(CreateBuildInfoTaskDelegate)
    KayTeeExtension ktConfig

    def setup() {
        Project project = ProjectBuilder.builder().build()
        ktConfig = new KayTeeExtension()
        project.extensions.add("kaytee", ktConfig)
        task = project.getTasks().create("buildInfo", CreateBuildInfoTask.class)
        task.delegate = delegate
    }

    def "Write build info"() {

        when:
        task.writeInfo()

        then:
        1 * delegate.writeInfo()

    }



    def "exception from delegate"() {

        when:
        task.writeInfo()

        then:
        1 * delegate.writeInfo() >> { throw new NullPointerException() }
        thrown(GradleException)
    }
}
