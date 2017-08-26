package uk.q3c.kaytee.plugin

import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.local.GitLocalConfiguration
import uk.q3c.util.version.VersionNumber
import uk.q3c.util.version.VersionNumberKt

import java.time.OffsetDateTime
/**
 * Created by David Sowerby on 06 Dec 2016
 */
class CreateBuildInfoTaskDelegateTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    CreateBuildInfoTaskDelegate delegate
    GitSHA gitSHA
    VersionNumber versionObject
    Project project = Mock(Project)
    Logger logger = Mock(Logger)
    File projectDir
    File buildDir
    File propertiesFile
    String version = "0.9.9.9"
    KayTeeExtension ktConfig
    ExtensionContainer projectExtensions = Mock(ExtensionContainer)
    GitLocalConfiguration wikiConfiguration
    SetVersionTaskDelegate versionCheck = Mock(SetVersionTaskDelegate)
    ExtraPropertiesExtension ext = Mock(ExtraPropertiesExtension)

    def setup() {
        ktConfig = new KayTeeExtension()
        gitSHA = testSha()

        temp = temporaryFolder.getRoot()
        projectDir = new File(temp, "projectDir")
        buildDir = new File(projectDir, "build")

        project.property("version") >> version
        project.name >> "testProject"
        project.projectDir >> projectDir
        project.buildDir >> buildDir
        propertiesFile = new File(buildDir, "resources/main/buildInfo.properties")

        versionObject = VersionNumberKt.parseFullVersionNumber(version)
        project.version >> versionObject
        project.getLogger() >> logger
        project.extensions >> projectExtensions
        projectExtensions.getByName("kaytee") >> ktConfig
        projectExtensions.getExtraProperties() >> ext
        delegate = new CreateBuildInfoTaskDelegate(project, versionCheck)
    }

    def "Write build info"() {
        given:
        ktConfig.version.number = version
        ext.get(KayTeePlugin.KAYTEE_COMMIT_ID) >> testSha().sha

        when:
        delegate.writeInfo()


        then:
        propertiesFile.exists()

        then:
        Properties properties = new Properties()
        properties.load(new FileInputStream(propertiesFile))
        properties.get("version") == version
        properties.get(CreateBuildInfoTaskDelegate.PROPERTY_NAME_COMMIT_ID) == gitSHA.sha
        properties.get("date") != null
        String dateAsString = properties.get("date")
        OffsetDateTime.parse(dateAsString).isBefore(OffsetDateTime.now())
        OffsetDateTime.parse(dateAsString).isAfter(OffsetDateTime.now().minusSeconds(1))
    }


    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }

    private GitSHA testSha1() {
        return new GitSHA(DigestUtils.sha1Hex('423'))
    }


}
