package uk.q3c.kaytee.plugin

import com.google.common.base.Splitter
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore
import spock.lang.Specification
import uk.q3c.build.creator.gradle.GradleGroovyBuilder

/**
 * Created by David Sowerby on 23 Aug 2016
 */
class KayTeePluginFTest extends Specification {

    @Rule
    final TemporaryFolder tempFolder = new TemporaryFolder()
    File buildFile
    File projectDir
    File buildDir
    GradleGroovyBuilder gradleFile
    final String versionUnderTest = '0.9.16.60'
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

    @Ignore ("use external project for functional testing")
    def "apply with no configuration changes"() {
        given:
        applyThisPlugin()
        gradleFile.execute()

        when:
        decodeResult(GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments("-PbintrayKey=xxxxx")
                .build())


        then:
//        confirmPluginsContains('JavaPlugin', 'GroovyPlugin', 'MavenPlugin', 'MavenPublishPlugin', 'KayTeePlugin')
        //gradle changes camel case to open case - 'integrationTest' becomes 'integration test
        confirmSourceSetsContains('test', 'main', 'integration test', 'production test', 'acceptance test', 'functional test')
        confirmTasksContains("':test'", "':integrationTest'", "':acceptanceTest'", "':functionalTest'", "':productionTest'")
        confirmQualityTasksContains('integrationTestQualityGate', 'acceptanceTestQualityGate', 'functionalTestQualityGate', 'productionTestQualityGate')
        confirmQualityTasksContains('testQualityGate')
    }

    def confirmTasksContains(String... tasks) {
        return elementSetContains('tasks', tasks)
    }

    def confirmQualityTasksContains(String... tasks) {
        return elementSetContains('tasks', tasks)
    }

    void decodeResult(BuildResult buildResult) {
        result = buildResult
        output = result.output
        outputLines = new TreeMap<>()
        List<String> outputList = Splitter.on('\n').omitEmptyStrings().splitToList(output)
        for (String s : outputList) {
            if (s.contains(':')) {
                int breakAt = s.indexOf(':')
                String key = s.substring(0, breakAt)
                String value = s.substring(breakAt)
                outputLines.put(key, value)
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
                println "unable to find element: " + st
                return false
            }
        }
        return true
    }


    private void buildscript() {
        gradleFile.buildscript().repositories().mavenLocal().jcenter()
        gradleFile.buildscript().dependencies().dependencies('classpath', 'uk.q3c.kaytee:kaytee-plugin:' + versionUnderTest)
        gradleFile.buildscript().dependencies().dependencies('classpath', 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3')
    }

    private void applyThisPlugin() {
        gradleFile.applyPlugin('uk.q3c.kaytee')
        gradleFile.line("ext.baseVersion = '0.9.9'")
    }


}
