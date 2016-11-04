package uk.q3c.simplycd.lifecycle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.gitplus.creator.gradle.ElementFactory
import uk.q3c.build.gitplus.creator.gradle.GradleFile
import uk.q3c.build.gitplus.creator.gradle.GradleFileContent

/**
 * Created by David Sowerby on 23 Aug 2016
 */
class SimplyCDPluginTest2 extends Specification {

    @Rule
    final TemporaryFolder tempFolder = new TemporaryFolder()
    File buildFile
    File projectDir
    File buildDir
    GradleFile gradleFile
    GradleFileContent gradleFileContent
    final String versionUnderTest = '0.1.2.2'


    def setup() {
        projectDir = tempFolder.getRoot()
        gradleFile = ElementFactory.INSTANCE.gradleFile(projectDir)
        gradleFileContent = gradleFile.getContent()
        buildDir = new File(projectDir, "build")
        buildFile = tempFolder.newFile('build.gradle')
        buildscript()
    }

    def "testSets not applied first, throw exception"() {
        given:
        applyThisPlugin()
        gradleFileContent.writeToFile(buildFile)

        when:
        GradleRunner.create()
                .withProjectDir(projectDir)
                .build()


        then:
        UnexpectedBuildFailure ex = thrown()
        ex.getMessage().contains("apply plugin 'org.unbroken-dome.test-sets' before this plugin ('uk.q3c.simplycd')")
    }


    def "testSets applied, quality gate and report tasks added with dependencies"() {
        expect: false

    }

    def "default test set only, quality gate and report tasks added with dependencies"() {
        given:
        applyTestSetPlugin()
        applyThisPlugin()
        gradleFileContent.writeToFile(buildFile)

        when:
        GradleRunner.create()
                .withProjectDir(projectDir)
                .build()

        then:
        noExceptionThrown()
    }

    private void buildscript() {
        gradleFileContent.buildscript().repositories().mavenLocal().jcenter().end().dependencies().dependencies('classpath', 'uk.q3c.simplycd:simplycd-lifecycle:' + versionUnderTest)
    }

    private void applyThisPlugin() {
        gradleFileContent.apply('uk.q3c.simplycd')
    }

    private void helloWorldTask() {
//        gradleFileContent.task("'helloWorld'").lines('doLast {', "println 'Hello world!'", "}")
        gradleFileContent.lines(" task ('helloWorld') << ", "{", "println 'helloWorld'", "}")
    }

    private void applyTestSetPlugin() {
        gradleFileContent.plugins("org.unbroken-dome.test-sets")
        gradleFileContent.repositories().jcenter()

    }

}
