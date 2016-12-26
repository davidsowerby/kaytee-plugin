package uk.q3c.simplycd.lifecycle

import com.google.common.collect.ImmutableList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSet
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer

import javax.inject.Inject

/**
 * Created by David Sowerby on 19 Dec 2016
 */
public class SimplyCDPlugin implements Plugin<Project> {

    final static String CREATE_BUILD_INFO_TASK_NAME = 'createBuildInfo'
    final static String GENERATE_CHANGE_LOG_TASK_NAME = 'generateChangeLog'
    final
    static List<String> defaultTestSets = ImmutableList.of("integrationTest", "functionalTest", "acceptanceTest", "smokeTest")

    private final Instantiator instantiator


    @Inject
    public SimplyCDPlugin(Instantiator instantiator) {
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

        // the default 'test' set will not trigger the listener, so we need to force it
        TestSetListener testSetListener = instantiator.newInstance TestSetListener, project
        testSetListener.addQualityGateTaskName('test')



        DefaultTestSetContainer container = project.testSets
        for (String ts : defaultTestSets) {
            container.add(new DefaultTestSet(ts))
        }
        project.tasks.create(CREATE_BUILD_INFO_TASK_NAME, CreateBuildInfoTask)
        project.tasks.create(GENERATE_CHANGE_LOG_TASK_NAME, GenerateChangeLogTask)
        project.version = new SimplyCDVersion(project)

        project.extensions.create('simplycd', SimplyCDContainer)
        project.afterEvaluate(new AfterEvaluateAction(project))
    }

    private void repositories(Project project) {
        project.repositories {
            mavenLocal()
            jcenter()
            mavenCentral()
        }

    }
}

class SimplyCDContainer {

    String remoteRepoUserName = 'davidsowerby'
}

