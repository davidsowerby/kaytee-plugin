package uk.q3c.kaytee.plugin

import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.merge.MergeStrategy
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration
import uk.q3c.build.gitplus.local.WikiLocal
import uk.q3c.build.gitplus.remote.GitRemote
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration

/**
 * Created by David Sowerby on 20 May 2017
 */
class MergeToMasterDelegateTaskTest extends Specification {

    MergeToMasterDelegateTask delegate
    Project project = Mock(Project)
    Logger logger = Mock(Logger)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    KayTeeExtension ktExtension
    GitPlus gitPlus = Mock(GitPlus)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    GitLocalConfiguration localConfiguration = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfiguration = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfiguration = Mock(GitRemoteConfiguration)


    def setup() {
        gitPlus.local >> gitLocal
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.remote >> gitRemote
        gitLocal.configuration >> localConfiguration
        wikiLocal.configuration >> wikiConfiguration
        gitRemote.configuration >> remoteConfiguration
        ktExtension = new KayTeeExtension()
        delegate = new MergeToMasterDelegateTask(project)
        project.logger >> logger
        project.extensions >> extensions
        extensions.getByName("kaytee") >> ktExtension
        gitLocal.masterBranch() >> new GitBranch("master")
    }

    def "merge"() {
        given:
        delegate.gitPlus = gitPlus

        when:
        delegate.merge()

        then:
        1 * localConfiguration.copyFrom(ktExtension.gitLocalConfiguration)
        1 * remoteConfiguration.copyFrom(ktExtension.gitRemoteConfiguration)
        1 * wikiConfiguration.copyFrom(ktExtension.wikiLocalConfiguration)

        then:
        1 * delegate.gitPlus.local.checkoutBranch(new GitBranch("master"))

        then:
        1 * delegate.gitPlus.local.mergeBranch(new GitBranch("kaytee"), MergeStrategy.THEIRS, MergeCommand.FastForwardMode.FF)

        then:
        1 * gitLocal.push(true, false)
    }


}
