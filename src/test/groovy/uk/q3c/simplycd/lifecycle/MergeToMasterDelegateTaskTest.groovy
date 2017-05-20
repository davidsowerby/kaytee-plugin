package uk.q3c.simplycd.lifecycle

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
    SimplyCDProjectExtension simplyCdExtension
    GitPlus gitPlus = Mock(GitPlus)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    GitLocalConfiguration localConfiguration = Mock()
    GitLocalConfiguration wikiConfiguration = Mock()
    GitRemoteConfiguration remoteConfiguration = Mock()


    def setup() {
        gitPlus.local >> gitLocal
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.remote >> gitRemote
        gitLocal.localConfiguration >> localConfiguration
        wikiLocal.localConfiguration >> wikiConfiguration
        gitRemote.configuration >> remoteConfiguration
        simplyCdExtension = new SimplyCDProjectExtension()
        delegate = new MergeToMasterDelegateTask(project)
        project.logger >> logger
        project.extensions >> extensions
        extensions.getByName("simplycd") >> simplyCdExtension
        gitLocal.masterBranch() >> new GitBranch("master")
    }

    def "merge"() {
        given:
        delegate.gitPlus = gitPlus

        when:
        delegate.merge()

        then:
        1 * localConfiguration.copyFrom(simplyCdExtension.gitLocalConfiguration)
        1 * remoteConfiguration.copyFrom(simplyCdExtension.gitRemoteConfiguration)
        1 * wikiConfiguration.copyFrom(simplyCdExtension.wikiLocalConfiguration)

        then:
        1 * delegate.gitPlus.local.checkoutBranch(new GitBranch("master"))

        then:
        1 * delegate.gitPlus.local.mergeBranch(new GitBranch("simplycd"), MergeStrategy.THEIRS, MergeCommand.FastForwardMode.FF)

        then:
        1 * gitLocal.push(true, false)
    }


}
