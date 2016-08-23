package uk.q3c.simplycd.lifecycle;

import com.google.common.collect.ImmutableSet;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer;

import java.io.File;
import java.util.Set;


/**
 * Created by David Sowerby on 08 Aug 2016
 */
@SuppressWarnings("MethodReturnOfConcreteClass")

public class SimplyCDPlugin implements Plugin<Project> {

    private static final String TEST_SETS_PLUGIN_NAME = "org.unbroken-dome.test-sets";
    private static final String SIMPLY_CD_PLUGIN_NAME = "uk.q3c.simplycd";
    private File reportsOutputDir;

    @Override
    public void apply(Project project) {
        if (reportsOutputDir == null) {
            reportsOutputDir = new File(project.getBuildDir(), "reports/jacoco");
        }
        Plugin testSetsPlugin = project.getPlugins()
                                       .findPlugin(TEST_SETS_PLUGIN_NAME);
        if (testSetsPlugin == null) {
            throw new GradleException("apply plugin '" + TEST_SETS_PLUGIN_NAME + "' before this plugin ('" + SIMPLY_CD_PLUGIN_NAME + "')");
        }
        final DefaultTestSetContainer testSetsConfig = (DefaultTestSetContainer) project.getExtensions()
                                                                                        .findByName("testSets");
        if (testSetsConfig == null) {
            throw new GradleException("Unable to locate test sets configuration");
        }
        System.out.println("checking groups");
        Set<TestSet> testGroups = ImmutableSet.copyOf(testSetsConfig);
        testGroups.forEach(tg -> {
            addTaskSet(project, tg.getTestTaskName());
        });

    }

    /**
     * Adds tasks for jacoco quality gate, and the jacoco test report needed to provide the input to it.
     *
     * @param project
     * @param testGroupName
     */
    @SuppressWarnings("StringConcatenationMissingWhitespace")

    private void addTaskSet(Project project, String testGroupName) {
        File reportOutputDir = new File(reportsOutputDir, testGroupName);

        Test testTask = (Test) project.getTasks()
                                      .findByName(testGroupName);

        if (testTask == null) {
            throw new GradleException("Unable to locate test task");
        }

        System.out.println("Located test task");
        System.out.println("bin results: " + testTask.getBinResultsDir());
        final QualityGateTask qualityGateTask = project.getTasks()
                                                       .create(testGroupName + "QualityGate", QualityGateTask.class);
        System.out.println("created task: " + qualityGateTask.getName());
        qualityGateTask.setTestGroup(testGroupName);
        final String reportName = testGroupName + "Report";
        File reportOutputFile = new File(reportOutputDir, reportName + ".xml");
        final JacocoReport testReportTask = project.getTasks()
                                                   .create(reportName, JacocoReport.class);
        testReportTask.executionData(testTask);
        testReportTask.setSourceDirectories(project.files(new File(project.getProjectDir(), "src/main/java")));
        testReportTask.setClassDirectories(project.files(new File(project.getBuildDir(), "classes/main")));
        testReportTask.getReports()
                      .getXml()
                      .setEnabled(true);
//        System.out.println("get destination before: "+testReportTask.getReports().getXml().getDestination().getAbsolutePath());
//        testReportTask.getReports()
//                      .getXml()
//                      .setDestination(reportOutputFile);
//        System.out.println("get destination after: "+testReportTask.getReports().getXml().getDestination().getAbsolutePath());
        System.out.println("created task: " + testReportTask.getName());
        System.out.println("report output file: " + reportOutputFile.getAbsolutePath());

        testReportTask.dependsOn(testTask);
        qualityGateTask.dependsOn(testReportTask);
    }


}
