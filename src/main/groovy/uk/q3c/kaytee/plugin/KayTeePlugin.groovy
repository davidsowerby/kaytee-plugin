package uk.q3c.kaytee.plugin

import com.google.common.collect.ImmutableList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSet
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer

import javax.inject.Inject

import static uk.q3c.kaytee.plugin.TaskNames.*

/**
 * Created by David Sowerby on 19 Dec 2016
 */
class KayTeePlugin implements Plugin<Project> {


    public final
    static List<String> testSetNames = ImmutableList.of(UNIT_TEST, INTEGRATION_TEST, FUNCTIONAL_TEST, ACCEPTANCE_TEST, PRODUCTION_TEST)

    private final Instantiator instantiator


    @Inject
    KayTeePlugin(Instantiator instantiator) {
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
        project.apply plugin: 'com.jfrog.bintray'
        KayTeeExtension kt = extensionWithBasicSettings(project)
        publishing(project)
        repositories(project)
        defaultProperties(project)
        testSets(project)


        Task t = project.tasks.create(GENERATE_BUILD_INFO_TASK_NAME, CreateBuildInfoTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(GENERATE_CHANGE_LOG_TASK_NAME, GenerateChangeLogTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(GENERATE_CONFIG_TASK_NAME, ConfigToJsonTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(MERGE_TO_MASTER, MergeToMasterTask)
        project.logger.debug("added task " + t.getName())

        config(project)
        project.version = new KayTeeVersion(project)
        bintray(project)
        project.afterEvaluate(new AfterEvaluateAction(project))
    }

    KayTeeExtension extensionWithBasicSettings(Project project) {
        KayTeeExtension extension = project.extensions.create('kaytee', KayTeeExtension)
        extension.gitLocalConfiguration.projectName = project.name
        extension.gitLocalConfiguration.projectDirParent = project.projectDir.parentFile
        return extension
    }

    void config(Project project) {

//        ThresholdsContainer thresholds = project.extensions.create('thresholds', ThresholdsContainer, instantiator)
//        thresholds.add(new TestGroupThresholds('test'))
//
//        for (String name : defaultTestSets) {
//            thresholds.add(new TestGroupThresholds(name))
//        }
    }


    private void testSets(Project project) {
        // a test set for 'test' is automatically created by gradle-testsets-plugin
        // but does not trigger the listener, so we need to force it
        TestSetListener testSetListener = instantiator.newInstance TestSetListener, project
        testSetListener.addQualityGateTaskName('test')

// create the other test sets
        DefaultTestSetContainer container = project.testSets
        for (String ts : testSetNames) {
            if (ts != UNIT_TEST) {
                container.add(new DefaultTestSet(ts))
            }
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void defaultProperties(Project project) {
        project.sourceCompatibility = '1.8'
    }

    private void repositories(Project project) {
        project.repositories {
            mavenLocal()
            jcenter()
            mavenCentral()
        }
    }

    void publishing(Project project) {
        Task sourcesJarTask = project.tasks.create("sourcesJar", Jar.class)
        sourcesJarTask.dependsOn('classes')
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.from(project.sourceSets.main.allSource)

        Task javadocJarTask = project.tasks.create("javadocJar", Jar.class)
        javadocJarTask.dependsOn('classes')
        javadocJarTask.classifier = 'javadoc'
        javadocJarTask.from(project.javadoc.destinationDir)

        project.logger.debug("pub block")
        project.publishing {
            publications {
                mavenStuff(MavenPublication) {
                    from project.components.java

                    artifact project.sourcesJar {
                        classifier "sources"
                    }

                    artifact project.javadocJar {
                        classifier "javadoc"
                    }
                }
            }
        }

        project.artifacts {
            archives project.sourcesJar
            archives project.javadocJar
        }
    }

    /**
     * Changes bintray defaults - other changes may be made in {@Link AfterEvaluateAction}
     *
     * @param project
     */
    void bintray(Project project) {
        project.bintray {
            publications = ['mavenStuff'] //When uploading Maven-based publication files
            publish = true
            dryRun = false
            pkg {
                version {
                    publicDownloadNumbers = true
                }
            }
        }
    }


}








