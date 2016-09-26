package uk.q3c.simplycd.lifecycle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer
import org.gradle.testkit.jarjar.com.google.common.collect.ImmutableSet
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer
import spock.lang.Specification

/**
 * Created by David Sowerby on 23 Aug 2016
 */
class SimplyCDPluginTest extends Specification {

    @Rule
    final TemporaryFolder tempFolder = new TemporaryFolder()
    File projectDir
    File buildDir

    Project project = Mock(Project)
    PluginContainer pluginContainer = Mock(PluginContainer)
    File buildFile
    SimplyCDPlugin plugin
    Plugin testSetPlugin = Mock(Plugin)
    ExtensionContainer extensionsContainer = Mock(ExtensionContainer)
    DefaultTestSetContainer testSetContainer = Mock(DefaultTestSetContainer)
    TestSet testSet1 = Mock(TestSet)
    Set<TestSet> testSets
    Test wigglyTestTask = Mock(Test)
    TaskContainer taskContainer = Mock(TaskContainer)
    QualityGateTask qualityGateTask = Mock(QualityGateTask)
    JacocoReport reportTask = Mock(JacocoReport)
    JacocoReportsContainer reportsContainer = Mock(JacocoReportsContainer)
    SingleFileReport xmlReport = Mock(SingleFileReport)
    final String testGroupName = 'wigglyTest'
    final String testReportName = testGroupName + "Report"
    Project realProject

    def setup() {
        projectDir = tempFolder.getRoot()
        buildDir = new File(projectDir, "build")
        realProject = ProjectBuilder.builder().build()
        buildFile = tempFolder.newFile('build.gradle')
        project.getPlugins() >> pluginContainer
        project.getExtensions() >> extensionsContainer
        project.getTasks() >> taskContainer
        project.getProjectDir() >> projectDir
        project.getBuildDir() >> buildDir
        plugin = new SimplyCDPlugin()
        testSet1.getTestTaskName() >> testGroupName
        reportTask.getReports() >> reportsContainer
        reportsContainer.getXml() >> xmlReport
    }

    def "testSets not applied first, throw exception"() {
        given:
        pluginContainer.findPlugin(SimplyCDPlugin.TEST_SETS_PLUGIN_NAME) >> null

        when:
        plugin.apply(project)

        then:
        GradleException ex = thrown()
        ex.message == "apply plugin 'org.unbroken-dome.test-sets' before this plugin ('uk.q3c.simplycd')"
    }


    def "testSets applied, quality gate and report tasks added with dependencies"() {
        given:
        pluginContainer.findPlugin(SimplyCDPlugin.TEST_SETS_PLUGIN_NAME) >> testSetPlugin
        project.getExtensions() >> extensionsContainer
        extensionsContainer.findByName('testSets') >> testSetContainer
        testSets = ImmutableSet.of(testSet1)
        testSetContainer.toArray() >> testSets.toArray()
        taskContainer.findByName(testGroupName) >> wigglyTestTask
        ConfigurableFileCollection sourceFiles = realProject.files(new File(projectDir, 'src/main/java'))
        ConfigurableFileCollection classFiles = realProject.files(new File(buildDir, 'classes/main'))
        project.files(_) >>> [sourceFiles, classFiles]



        when:
        plugin.apply(project)

        then:
        1 * taskContainer.create(testGroupName + "QualityGate", QualityGateTask.class) >> qualityGateTask
        1 * qualityGateTask.setTestGroup(testGroupName)
        1 * taskContainer.create(testReportName, JacocoReport.class) >> reportTask
        1 * reportTask.executionData(wigglyTestTask)
        1 * xmlReport.setEnabled(true)
        1 * reportTask.dependsOn(wigglyTestTask)
        1 * qualityGateTask.dependsOn(reportTask)
        1 * reportTask.setClassDirectories(classFiles)
        1 * reportTask.setSourceDirectories(sourceFiles)

    }


}
