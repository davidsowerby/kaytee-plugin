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
@SuppressWarnings({"MethodReturnOfConcreteClass", "ClassWithoutConstructor", "ClassHasNoToStringMethod", "ClassWithoutLogger"})

public class SimplyCDPlugin implements Plugin<Project> {

    private static final String TEST_SETS_PLUGIN_NAME = "org.unbroken-dome.test-sets";
    private static final String SIMPLY_CD_PLUGIN_NAME = "uk.q3c.simplycd";
    private Project project;

    @Override
    @SuppressWarnings({"ParameterNameDiffersFromOverriddenParameter", "PublicMethodWithoutLogging"})
    public void apply(Project project) {
        this.project = project;

        //noinspection rawtypes type not needed
        final Plugin testSetsPlugin = project.getPlugins()
                                             .findPlugin(TEST_SETS_PLUGIN_NAME);
        if (testSetsPlugin == null) {
            throw new GradleException("apply plugin '" + TEST_SETS_PLUGIN_NAME + "' before this plugin ('" + SIMPLY_CD_PLUGIN_NAME + "')");
        }

        //noinspection TypeMayBeWeakened
        final DefaultTestSetContainer testSetsConfig = (DefaultTestSetContainer) project.getExtensions()
                                                                                        .findByName("testSets");
        final Set<TestSet> testGroups = ImmutableSet.copyOf(testSetsConfig);
        testGroups.forEach(tg -> addTaskSet(tg.getTestTaskName()));

    }

    /**
     * Adds tasks for jacoco quality gate, and the jacoco test report needed to provide the input to it.
     *
     * @param testGroupName for example, 'integrationTest
     */
    @SuppressWarnings({"StringConcatenationMissingWhitespace", "HardcodedFileSeparator", "DuplicateStringLiteralInspection"})
    private void addTaskSet(String testGroupName) {
        final Test testTask = (Test) project.getTasks()
                                            .findByName(testGroupName);
        final QualityGateTask qualityGateTask = project.getTasks()
                                                       .create(testGroupName + "QualityGate", QualityGateTask.class);
        qualityGateTask.setTestGroup(testGroupName);
        final String reportName = testGroupName + "Report";
        final JacocoReport testReportTask = project.getTasks()
                                                   .create(reportName, JacocoReport.class);
        testReportTask.executionData(testTask);

        final String javaSourcePath = "src/main/java";
        testReportTask.setSourceDirectories(project.files(new File(project.getProjectDir(), javaSourcePath)));

        final String classesPath = "classes/main";
        testReportTask.setClassDirectories(project.files(new File(project.getBuildDir(), classesPath)));
        testReportTask.getReports()
                      .getXml()
                      .setEnabled(true);

        testReportTask.dependsOn(testTask);
        qualityGateTask.dependsOn(testReportTask);
    }


}
