package uk.q3c.kaytee.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin
import uk.q3c.build.gitplus.GitPlusFactory
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch

import javax.inject.Inject

import static uk.q3c.kaytee.plugin.TaskNames.*
/**
 * Created by David Sowerby on 19 Dec 2016
 */
class KayTeePlugin implements Plugin<Project> {


    private final Instantiator instantiator
    public final static String KAYTEE_CONFIG_FLAG = "kayteeIsConfigured"
    public final static String KAYTEE_COMMIT_ID = "kayteeCommitId"
    public final static String KAYTEE_RERUN = "kayteeIsRerun"


    @Inject
    KayTeePlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }


    @Override
    void apply(Project project) {
        // we want to stop the KayTee from attempting to read Git before config has been evaluated
        // so we create a flag here
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        ext.set(KAYTEE_CONFIG_FLAG, false)
        // We will want the commit id for a number of things, so get it here once.  The only other thing we need Git for
        // is tags, in VersionCheckTask
        GitPlus gitPlus = GitPlusFactory.instance
        gitPlus.remote.active = false
        gitPlus.local.projectDirParent = project.projectDir.parentFile
        gitPlus.local.projectName = project.name
        gitPlus.execute()

        // when using CI server we expect the 'kaytee' branch to be HEAD, but if Gradle is executed locally we just accept
        // the current branch
        GitBranch branch = gitPlus.local.currentBranch()
        GitSHA headSha = gitPlus.local.headCommitSHA(branch)
        String headCommitId = headSha.sha
        ext.set(KAYTEE_COMMIT_ID, headCommitId)
        project.logger.lifecycle("Building commit: $headCommitId for branch: ${branch.name}")

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
        defaultTestSet(project)


        Task t = project.tasks.create(GENERATE_BUILD_INFO_TASK_NAME, CreateBuildInfoTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(GENERATE_CHANGE_LOG_TASK_NAME, GenerateChangeLogTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(EXTRACT_CONFIG_TASK_NAME, ConfigToJsonTask)
        project.logger.debug("added task " + t.getName())
        t = project.tasks.create(MERGE_TO_MASTER, MergeToMasterTask)
        project.logger.debug("added task " + t.getName())
        Task setVersionTask = project.tasks.create(SET_VERSION, VersionCheckTask)
        project.logger.debug("added task " + setVersionTask.getName())
        t = project.tasks.create(TAG, TagTask)
        t.dependsOn(setVersionTask)
        project.logger.debug("added task " + t.getName())
        Task bintrayConfigToJson = project.tasks.create(BINTRAY_CONFIG_TO_JSON, BintrayConfigToJsonTask)
        project.logger.debug("added task " + bintrayConfigToJson.getName())


        bintray(project)
        project.afterEvaluate(new AfterEvaluateAction(project))
    }


    KayTeeExtension extensionWithBasicSettings(Project project) {
        KayTeeExtension extension = project.extensions.create('kaytee', KayTeeExtension)
        extension.gitLocalConfiguration.projectName = project.name
        extension.gitLocalConfiguration.projectDirParent = project.projectDir.parentFile
        return extension
    }

    private void defaultTestSet(Project project) {
        // a test set for 'test' is automatically created by gradle-testsets-plugin
        // but does not trigger the listener, so we need to force it
        TestSetListener testSetListener = instantiator.newInstance TestSetListener, project
        testSetListener.addQualityGateTaskName('test')
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








