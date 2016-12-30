package uk.q3c.simplycd.lifecycle

import org.apache.commons.io.FileUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.util.testutil.TestResource

/**
 *
 *
 * Created by David Sowerby on 09 Aug 2016
 */
class QualityGateTaskTest extends Specification {

    QualityGateTask task

    @Rule
    TemporaryFolder temporaryFolder
    Project project

    File temp
    File projectDir
    SimplyCDContainer simplyCDContainer
    TestConfiguration testConfiguration

    def setup() {
        temp = temporaryFolder.getRoot()
        project = ProjectBuilder.builder().build()
        projectDir = project.getProjectDir()
        Instantiator instantiator = Mock(Instantiator)
        simplyCDContainer = project.extensions.create('simplycd', SimplyCDContainer.class, instantiator)
        testConfiguration = new TestConfiguration('integrationTest')
        testConfiguration.instruction = 100
        simplyCDContainer.add(testConfiguration)
        task = project.getTasks().create("qg", QualityGateTask.class);
        task.setTestGroup('integrationTest')
    }

    def "check fails thresholds"() {

        given:
        setupFile()
        testConfiguration.qualityGateEnabled = true

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('Code coverage failed')
    }

    def "results file missing"() {

        given:
        testConfiguration.qualityGateEnabled = true

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('FileNotFoundException occurred in QualityGateTask for integrationTest')
    }

    def "check passes because quality check is disabled"() {
        given:

        setupFile()

        when:
        task.evaluate()

        then:
        noExceptionThrown()
    }

    def "check passes"() {
        given:

        testConfiguration.qualityGateEnabled = true
        testConfiguration.instruction = 50
        testConfiguration.branch = 70
        testConfiguration.complexity = 66
        testConfiguration.line = 66
        testConfiguration.method = 66
        testConfiguration.clazz = 90


        setupFile()

        when:
        task.evaluate()

        then:
        noExceptionThrown()
        task.getThresholds().get("instruction") == new Double(50)
        task.testGroup == 'integrationTest'
    }


    def setupFile() {
        File reportDir = new File(projectDir, 'build/reports/jacoco/integrationTestReport')
        String reportFileName = 'integrationTestReport.xml'
        FileUtils.forceMkdir(reportDir)
        FileUtils.copyFileToDirectory(TestResource.resource(this, reportFileName), reportDir)

    }

}
