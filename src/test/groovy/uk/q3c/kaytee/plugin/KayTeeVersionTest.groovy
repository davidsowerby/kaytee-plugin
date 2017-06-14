package uk.q3c.kaytee.plugin

import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration
import uk.q3c.build.gitplus.local.WikiLocal
import uk.q3c.build.gitplus.remote.GitRemote
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration

/**
 * Created by David Sowerby on 10 May 2017
 */
class KayTeeVersionTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp

    GitPlus gitPlus = Mock(GitPlus)

    KayTeeVersion version
    Project project = Mock(Project)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    KayTeeExtension config

    GitLocalConfiguration localConfig = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfig = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfig = Mock(GitRemoteConfiguration)
    Logger logger = Mock(Logger)
    File projectDir

    def setup() {
        temp = temporaryFolder.getRoot()
        config = new KayTeeExtension()
        gitPlus.local >> gitLocal
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.remote >> gitRemote
        gitLocal.localConfiguration >> localConfig
        gitRemote.configuration >> remoteConfig
        wikiLocal.localConfiguration >> wikiConfig

        GitBranch branch = new GitBranch("kaytee")
        gitLocal.currentBranch() >> branch
        gitLocal.headCommitSHA(branch) >> testSha()
        project.getExtensions() >> extensions
        extensions.getByName("kaytee") >> config
        config.baseVersion = "1.2.3.4"
        version = new KayTeeVersion(project, gitPlus)
        project.logger >> logger
        projectDir = new File(temp, "wiggly")
        project.projectDir >> projectDir
    }

    def "version"() {

        when:
        String v = version.toString()

        then:
        v == "1.2.3.4." + testSha().short()
        println version.toString()
    }

    def "baseVersionFromFullVersion"() {
        given:
        String fullVersion = config.baseVersion + ".55"

        expect:
        version.baseVersionFromFullVersion(fullVersion) == "1.2.3.4"


    }

    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }
}
