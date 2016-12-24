package uk.q3c.simplycd.lifecycle

import com.google.common.base.Splitter
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.creator.gradle.GradleGroovyBuilder
/**
 * Created by David Sowerby on 23 Aug 2016
 */
class SimplyCDPluginTest2 extends Specification {

    @Rule
    final TemporaryFolder tempFolder = new TemporaryFolder()
    File buildFile
    File projectDir
    File buildDir
    GradleGroovyBuilder gradleFile
    final String versionUnderTest = '0.1.5.5'
    String output
    BuildResult result
    Map<String, String> outputLines


    def setup() {
        projectDir = tempFolder.getRoot()
        gradleFile = new GradleGroovyBuilder()
        buildDir = new File(projectDir, "build")
        buildFile = tempFolder.newFile('build.gradle')
        gradleFile.outputDir = projectDir

        buildscript()
    }

    def "apply with no configuration changes"() {
        given:
        applyThisPlugin()
        gradleFile.execute()

        when:
        decodeResult(GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments('properties')
                .build())


        then:
        confirmPluginsContains('JavaPlugin', 'GroovyPlugin', 'MavenPlugin', 'MavenPublishPlugin', 'SimplyCDPlugin', 'BintrayPlugin')
        //gradle changes camel case to open case - 'integrationTest' becomes 'integration test
        confirmSourceSetsContains('test', 'main', 'integration test', 'smoke test', 'acceptance test', 'functional test')
    }


    void decodeResult(BuildResult buildResult) {
        result = buildResult
        output = result.output
        outputLines = new TreeMap<>()
        List<String> outputList = Splitter.on('\n').omitEmptyStrings().splitToList(output)
        for (String s : outputList) {
            if (s.contains(':')) {
                String[] r = s.split(":")
                outputLines.put(r[0], r[1])
            }
        }
    }

    def confirmPluginsContains(String... s) {
        return elementSetContains("plugins", s)
    }

    def confirmSourceSetsContains(String... s) {
        return elementSetContains("sourceSets", s)
    }

    def elementSetContains(String elementSetName, String... s) {
        String elements = outputLines.get(elementSetName)
        if (elements == null) {
            return false
        }
        for (String st : s) {
            if (!elements.contains(st)) {
                return false
            }
        }
        return true
    }


    private void buildscript() {
        gradleFile.buildscript().repositories().mavenLocal().jcenter()
        gradleFile.buildscript().dependencies().dependencies('classpath', 'uk.q3c.simplycd:simplycd-lifecycle:' + versionUnderTest)
        gradleFile.buildscript().dependencies().dependencies('classpath', 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3')
    }

    private void applyThisPlugin() {
        gradleFile.applyPlugin('uk.q3c.simplycd')
    }




}
