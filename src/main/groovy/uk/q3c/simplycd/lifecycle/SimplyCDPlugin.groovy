package uk.q3c.simplycd.lifecycle

import com.google.common.collect.ImmutableList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSet
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer

import javax.inject.Inject

import static uk.q3c.simplycd.lifecycle.TaskNames.*
/**
 * Created by David Sowerby on 19 Dec 2016
 */
class SimplyCDPlugin implements Plugin<Project> {


    public final
    static List<String> defaultTestSets = ImmutableList.of(INTEGRATION_TEST, FUNCTIONAL_TEST, ACCEPTANCE_TEST, PRODUCTION_TEST)

    private final Instantiator instantiator


    @Inject
    SimplyCDPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }


    @Override
    void apply(Project project) {
        project.apply plugin: JavaPlugin
        project.apply plugin: GroovyPlugin
        project.apply plugin: 'maven'
        project.apply plugin: 'maven-publish'
        project.apply plugin: 'eclipse-wtp'
        project.apply plugin: 'idea'
        project.apply plugin: 'jacoco'
        project.apply plugin: TestSetsPlugin

        repositories(project)
        defaultProperties(project)
        testSets(project)

        Task t = project.tasks.create(CREATE_BUILD_INFO_TASK_NAME, CreateBuildInfoTask)
        project.logger.lifecycle("added task " + t.getName())
        t = project.tasks.create(GENERATE_CHANGE_LOG_TASK_NAME, GenerateChangeLogTask)
        project.logger.lifecycle("added task " + t.getName())
        t = project.tasks.create(GENERATE_CONFIG_TASK_NAME, ConfigToJsonTask)
        project.logger.lifecycle("added task " + t.getName())

        config(project)

        // actions required after evaluation, notably publishing
        project.afterEvaluate(new AfterEvaluateAction(project))

    }

    void config(Project project) {
        project.extensions.create('simplycd', SimplyCDProjectExtension)
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        config.wikiLocalConfiguration.active = true
        config.wikiLocalConfiguration.cloneFromRemote = true
        config.gitRemoteConfiguration.repoName = project.name
        config.gitRemoteConfiguration.repoUser = config.remoteRepoUserName
        config.gitLocalConfiguration.projectName = project.name
        config.gitLocalConfiguration.projectDirParent = project.projectDir.parentFile

        config.changeLog.projectName = project.name
        config.changeLog.projectDirParent = project.projectDir.parentFile
        config.changeLog.autoTagLatestCommit = false

        ThresholdsContainer thresholds = project.extensions.create('thresholds', ThresholdsContainer, instantiator)
        thresholds.add(new TestGroupThresholds('test'))

        for (String name : defaultTestSets) {
            thresholds.add(new TestGroupThresholds(name))
        }
    }


    private void testSets(Project project) {
        // the default 'test' set will not trigger the listener, so we need to force it
        TestSetListener testSetListener = instantiator.newInstance TestSetListener, project
        testSetListener.addQualityGateTaskName('test')


        DefaultTestSetContainer container = project.testSets
        for (String ts : defaultTestSets) {
            container.add(new DefaultTestSet(ts))
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void defaultProperties(Project project) {
        project.sourceCompatibility = '1.8'
        project.version = new SimplyCDVersion(project)
    }

    private void repositories(Project project) {
        project.repositories {
            mavenLocal()
            jcenter()
            mavenCentral()
        }
    }


}





