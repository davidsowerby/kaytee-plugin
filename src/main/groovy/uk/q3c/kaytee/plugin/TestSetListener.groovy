package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class TestSetListener {

    final Project project

    TestSetListener(Project project) {
        this.project = project
        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }

    void testSetAdded(TestSet testSet) {
        project.logger.lifecycle("Adding test set for: " + testSet.testTaskName)
        addQualityGateTask(testSet)
    }

    void addQualityGateTask(TestSet testSet) {
        addQualityGateTaskName(testSet.testTaskName)
    }

    void addQualityGateTaskName(String testGroupName) {
        log("adding quality gate task for test set: " + testGroupName)
        final QualityGateTask qualityGateTask = project.tasks.create(testGroupName + "QualityGate", QualityGateTask.class)
        qualityGateTask.testGroup = TaskKey.fromGradleTask(testGroupName)
        final String reportName = testGroupName + "Report"
        final JacocoReport testReportTask = project.tasks.create(reportName, JacocoReport.class)

        final def testTask = project.tasks.findByName(testGroupName)
        testReportTask.executionData(testTask)

        final String javaSourcePath = "src/main/java"
        final ConfigurableFileCollection sourceFiles = project.files(new File(project.projectDir, javaSourcePath))
        testReportTask.setSourceDirectories(sourceFiles)

        final String classesPath = "classes/main"
        final ConfigurableFileCollection classFiles = project.files(new File(project.buildDir, classesPath))
        testReportTask.setClassDirectories(classFiles)
        testReportTask.getReports()
                .getXml()
                .setEnabled(true)

        testReportTask.dependsOn(testTask)
        qualityGateTask.dependsOn(testReportTask)
    }


    private void log(String msg) {
        project.logger.lifecycle(msg)
    }
}
