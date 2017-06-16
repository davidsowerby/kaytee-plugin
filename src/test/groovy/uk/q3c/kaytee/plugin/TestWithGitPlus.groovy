package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtraPropertiesExtension
import spock.lang.Specification
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration
import uk.q3c.build.gitplus.local.WikiLocal
import uk.q3c.build.gitplus.remote.GitRemote
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration

/**
 * Created by David Sowerby on 16 Jun 2017
 */
class TestWithGitPlus extends Specification {

    GitPlus gitPlus = Mock(GitPlus)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    GitLocalConfiguration localConfiguration = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfiguration = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfiguration = Mock(GitRemoteConfiguration)
    Project project = Mock(Project)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    ExtraPropertiesExtension ext = Mock(ExtraPropertiesExtension)
    Logger logger = Mock(Logger)
    KayTeeExtension config = Mock(KayTeeExtension)


    def setup() {
        gitPlus.local >> gitLocal
        gitLocal.localConfiguration >> localConfiguration
        gitLocal.currentBranch() >> new GitBranch("kaytee")
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.wikiLocal.localConfiguration >> wikiConfiguration
        gitPlus.remote >> gitRemote
        gitRemote.configuration >> remoteConfiguration
        project.getExtensions() >> extensions
        project.logger >> logger
        extensions.extraProperties >> ext
        project.extensions.getByName("kaytee") >> config
    }
}
