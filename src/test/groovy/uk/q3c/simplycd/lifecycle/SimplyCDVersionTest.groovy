package uk.q3c.simplycd.lifecycle

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.gitplus.GitSHA

/**
 * Created by David Sowerby on 10 May 2017
 */
class SimplyCDVersionTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    SimplyCDVersion version
    Project project
    File buildDir
    Logger logger = Mock(Logger)

    def setup() {
        temp = temporaryFolder.getRoot()
        buildDir = new File(temp, "build")
        project = Mock(Project)
        project.buildDir >> buildDir
        version = new SimplyCDVersion(project)
        project.logger >> logger
    }


    def "version"() {
        given:
        Properties properties = new Properties()
        properties.put(CreateBuildInfoTaskDelegate.PROPERTY_NAME_COMMIT_ID, testSha().sha)
        FileUtils.forceMkdir(new File(buildDir, "resources/main"))
        File f = new File(buildDir, CreateBuildInfoTaskDelegate.PATH_TO_BUILD_INFO_PROPS)
        FileWriter fw = new FileWriter(f)
        properties.store(fw, "any")
        project.property("baseVersion") >> "0.9.8"

        expect:
        version.toString() == "0.9.8." + testSha().short()
    }

    def "no props file, uses 'dev' as build number"() {
        given:
        project.property("baseVersion") >> "0.9.8"

        expect:
        version.toString() == "0.9.8." + "dev"
    }

    def "no commit id in props file, uses 'dev' as build number"() {
        given:
        Properties properties = new Properties()
        FileUtils.forceMkdir(new File(buildDir, "resources/main"))
        File f = new File(buildDir, CreateBuildInfoTaskDelegate.PATH_TO_BUILD_INFO_PROPS)
        FileWriter fw = new FileWriter(f)
        properties.store(fw, "any")
        project.property("baseVersion") >> "0.9.8"

        expect:
        version.toString() == "0.9.8." + "dev"
    }

    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }
}
