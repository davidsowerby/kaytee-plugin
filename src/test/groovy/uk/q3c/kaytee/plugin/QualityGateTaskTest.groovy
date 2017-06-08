package uk.q3c.kaytee.plugin

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
    Project project

    File temp
    File projectDir
    KayTeeExtension kt
    TestGroupThresholds thresholds

    def setup() {
        temp = temporaryFolder.getRoot()
        project = ProjectBuilder.builder().build()
        projectDir = project.getProjectDir()
        kt = project.extensions.create('kaytee', KayTeeExtension.class)


        task = project.getTasks().create("qg", QualityGateTask.class)
        task.setTestGroup(TaskKey.Integration_Test)
    }

    def "check fails thresholds"() {

        given:
        setupFile()
        kt.integrationTest.qualityGate = true
        kt.integrationTest.thresholds.instruction = 100

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('Code coverage failed')
    }

    def "results file missing"() {

        given:
        kt.integrationTest.qualityGate = true

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('FileNotFoundException occurred in QualityGateTask for integrationTest')
    }

    def "check still fails when disabled - disabled refers only to KayTee server"() {
        given:
        setupFile()
        kt.integrationTest.qualityGate = true
        kt.integrationTest.thresholds.instruction = 100

        when:
        task.evaluate()

        then:
        GradleException ex = thrown()
        ex.getMessage().equals('Code coverage failed')
    }

    def "check passes"() {
        given:

        kt.integrationTest.qualityGate = true
        thresholds = kt.testConfig(TaskKey.Integration_Test).thresholds
        thresholds.instruction = 50
        thresholds.branch = 70
        thresholds.complexity = 66
        thresholds.line = 66
        thresholds.method = 66
        thresholds.clazz = 90


        setupFile()

        when:
        task.evaluate()

        then:
        noExceptionThrown()
        task.getThresholds().get("instruction") == new Double(50)
        task.testGroup.gradleTask() == 'integrationTest'
    }

    def "setting invalid test group throws exception"() {
        when:
        task.setTestGroup(TaskKey.Custom)

        then:
        thrown IllegalArgumentException
    }

    def setupFile() {
        File reportDir = new File(projectDir, 'build/reports/jacoco/integrationTestReport')
        String reportFileName = 'integrationTestReport.xml'
        FileUtils.forceMkdir(reportDir)
        FileUtils.copyFileToDirectory(TestResource.resource(this, reportFileName), reportDir)

    }

}
