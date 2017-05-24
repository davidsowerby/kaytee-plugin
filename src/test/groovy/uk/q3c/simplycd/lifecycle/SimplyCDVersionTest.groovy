package uk.q3c.simplycd.lifecycle

import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
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
class SimplyCDVersionTest extends Specification {

    GitPlus gitPlus = Mock(GitPlus)

    SimplyCDVersion version
    Project project = Mock(Project)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    SimplyCDProjectExtension config

    GitLocalConfiguration localConfig = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfig = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfig = Mock(GitRemoteConfiguration)
    Logger logger = Mock()

    def setup() {
        config = new SimplyCDProjectExtension()
        gitPlus.local >> gitLocal
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.remote >> gitRemote
        gitLocal.localConfiguration >> localConfig
        gitRemote.configuration >> remoteConfig
        wikiLocal.localConfiguration >> wikiConfig

        GitBranch branch = new GitBranch("simplycd")
        gitLocal.currentBranch() >> branch
        gitLocal.latestCommitSHA(branch) >> testSha()
        project.getExtensions() >> extensions
        extensions.getByName("simplycd") >> config
        version = new SimplyCDVersion(project, gitPlus)
        project.logger >> logger
    }

    def "version"() {
        given:
        config.baseVersion = "1.2.3.4"

        when:
        String v = version.toString()

        then:
        v == "1.2.3.4." + testSha().short()
        println version.toString()
    }

    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }
}
