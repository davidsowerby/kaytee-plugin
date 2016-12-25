package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by David Sowerby on 06 Dec 2016
 */
class CreateBuildInfoTaskTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    CreateBuildInfoTask task


    def setup() {
        temp = temporaryFolder.getRoot()
        Project project = ProjectBuilder.builder().build()
        task = project.getTasks().create("buildInfo", CreateBuildInfoTask.class);
    }

    def "Write build info"() {
        when:
        task.writeInfo()


        then:
        false
    }


}
