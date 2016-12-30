package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 29 Dec 2016
 */
class GenerateChangeLogTaskDelegateTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    File projectDir

    GenerateChangeLogTaskDelegate delegate
    ChangeLog changeLog = Mock(ChangeLog)
    Project project = Mock(Project)
    SimplyCDContainer simplyCDContainer = Mock(SimplyCDContainer)

    void setup() {
        temp = temporaryFolder.getRoot()
        projectDir = new File(temp, 'wiggly')
        delegate = new GenerateChangeLogTaskDelegate()
    }

    def "Generate"() {
        given:
        String name = 'wiggly'

        project.name >> name
        project.projectDir >> projectDir
        simplyCDContainer.remoteRepoUserName >> 'munchkin'

        when:
        delegate.generate(project, simplyCDContainer, changeLog)

        then:
        1 * changeLog.remoteRepoUser('munchkin')
        1 * changeLog.projectName(name)
        1 * changeLog.projectDirParent(temp)

        then:
        1 * changeLog.generate()
    }


}
