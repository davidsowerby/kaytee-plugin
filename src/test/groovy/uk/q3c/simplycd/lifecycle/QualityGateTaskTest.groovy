package uk.q3c.simplycd.lifecycle

import org.apache.commons.io.FileUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
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

    File temp
    File projectDir

    def setup() {
        temp = temporaryFolder.getRoot()
        Project project = ProjectBuilder.builder().build()
        projectDir = project.getProjectDir()
        task = project.getTasks().create("qg", QualityGateTask.class);
    }

    def "check fails thresholds"() {

        given:
        setupFile()

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('Code coverage failed')
    }

    def "results file missing"() {

        given:
        task.setTestGroup('IntegrationTest')

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('FileNotFoundException occurred in QualityGateTask for IntegrationTest')
    }

    def "check passes"() {
        given:
        setupFile()
        final Map<String, Integer> thresholds;
        thresholds = new HashMap<>(6);
        thresholds.put("instruction", 69);
        thresholds.put("branch", 70);
        thresholds.put("line", 66);
        thresholds.put("complexity", 66);
        thresholds.put("method", 66);
        thresholds.put("class", 90);

        task.setThresholds(thresholds)

        when:
        task.evaluate()

        then:
        noExceptionThrown()
    }


    def setupFile() {
        File reportDir = new File(projectDir, 'build/reports/jacoco/integrationTestReport')
        String reportFileName = 'integrationTestReport.xml'
        FileUtils.forceMkdir(reportDir)
        FileUtils.copyFileToDirectory(TestResource.resource(this, reportFileName), reportDir)
        task.setTestGroup('integrationTest')
    }

}
