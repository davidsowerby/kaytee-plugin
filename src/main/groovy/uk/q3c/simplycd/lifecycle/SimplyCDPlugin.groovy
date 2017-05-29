package uk.q3c.simplycd.lifecycle

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
        project.apply plugin: 'com.jfrog.bintray'
        SimplyCDProjectExtension simplycd = extensionWithBasicSettings(project)
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


        project.version = new SimplyCDVersion(project)
        bintray(project)
        project.afterEvaluate(new AfterEvaluateAction(project))
    }

    SimplyCDProjectExtension extensionWithBasicSettings(Project project) {
        SimplyCDProjectExtension extension = project.extensions.create('simplycd', SimplyCDProjectExtension)
        extension.gitLocalConfiguration.projectName = project.name
        extension.gitLocalConfiguration.projectDirParent = project.projectDir.parentFile
        return extension
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








