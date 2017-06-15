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
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration
/**
 * Created by David Sowerby on 10 May 2017
 */
class KayTeeVersionTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp

    KayTeeVersion version
    Project project = Mock(Project)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    KayTeeExtension config

    GitLocalConfiguration localConfig = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfig = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfig = Mock(GitRemoteConfiguration)
    Logger logger = Mock(Logger)
    File projectDir
    ExtraPropertiesExtension ext = Mock(ExtraPropertiesExtension)

    def setup() {
        temp = temporaryFolder.getRoot()
        config = new KayTeeExtension()

        project.getExtensions() >> extensions
        extensions.getByName("kaytee") >> config
        extensions.extraProperties >> ext
        config.baseVersion = "1.2.3.4"
        version = new KayTeeVersion(project)
        project.logger >> logger
        projectDir = new File(temp, "wiggly")
        project.projectDir >> projectDir
    }

    def "version"() {
        given:
        ext.get(KayTeePlugin.KAYTEE_CONFIG_FLAG) >> true
        ext.get(KayTeePlugin.KAYTEE_COMMIT_ID) >> testSha().sha

        when:
        String v = version.toString()

        then:
        v == "1.2.3.4." + testSha().short()
    }

    def "version not available until evaluation complete"() {

        when:
        String v = version.toString()

        then:
        v == "version not available until evaluation complete"
    }

    def "baseVersionFromFullVersion"() {
        when:
        String fullVersion = config.baseVersion + ".55"

        then:
        version.baseVersionFromFullVersion(fullVersion) == "1.2.3.4"

        when:
        fullVersion = "unspecified"

        then:
        version.baseVersionFromFullVersion(fullVersion) == "unspecified"

    }

    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }
}
