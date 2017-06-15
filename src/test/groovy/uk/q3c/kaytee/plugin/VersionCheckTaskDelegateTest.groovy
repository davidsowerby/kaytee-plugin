package uk.q3c.kaytee.plugin

import com.google.common.collect.ImmutableList
import org.eclipse.jgit.lib.PersonIdent
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.ExtraPropertiesExtension
import spock.lang.Specification
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.*
import uk.q3c.build.gitplus.remote.GitRemote
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration

import java.time.ZonedDateTime

/**
 * Created by David Sowerby on 13 Jun 2017
 */
class VersionCheckTaskDelegateTest extends Specification {

    VersionCheckTaskDelegate delegate
    Project project = Mock(Project)
    GitPlus gitPlus = Mock(GitPlus)
    GitLocal gitLocal = Mock(GitLocal)
    WikiLocal wikiLocal = Mock(WikiLocal)
    GitRemote gitRemote = Mock(GitRemote)
    KayTeeExtension config = new KayTeeExtension()
    ExtensionContainer extensions = Mock(ExtensionContainer)
    Logger logger = Mock(Logger)
    GitLocalConfiguration localConfiguration = Mock(GitLocalConfiguration)
    GitLocalConfiguration wikiConfiguration = Mock(GitLocalConfiguration)
    GitRemoteConfiguration remoteConfiguration = Mock(GitRemoteConfiguration)
    String sha0 = "1111111111111111111111111111111111111110"
    String sha1 = "1111111111111111111111111111111111111111"
    String sha2 = "1111111111111111111111111111111111111112"
    String sha3 = "1111111111111111111111111111111111111113"
    GitSHA headSHA = new GitSHA(sha0)
    PersonIdent personIdent
    GitCommit headCommit
    GitCommit commit1
    GitCommit commit2
    GitCommit commit3
    ExtraPropertiesExtension ext = Mock(ExtraPropertiesExtension)


    def setup() {
        personIdent = new PersonIdent("a", "B", new Date(), TimeZone.getTimeZone("GMT"))
        headCommit = new GitCommit("full message", sha0, personIdent, personIdent)
        commit1 = new GitCommit("full message", sha1, personIdent, personIdent)
        commit2 = new GitCommit("full message", sha2, personIdent, personIdent)
        commit3 = new GitCommit("full message", sha3, personIdent, personIdent)
        delegate = new VersionCheckTaskDelegate(project, gitPlus)
        gitPlus.local >> gitLocal
        gitLocal.localConfiguration >> localConfiguration
        gitLocal.currentBranch() >> new GitBranch("kaytee")
        gitLocal.headCommitSHA(new GitBranch("kaytee")) >> new GitSHA(sha0)
        gitPlus.wikiLocal >> wikiLocal
        gitPlus.wikiLocal.localConfiguration >> wikiConfiguration
        gitPlus.remote >> gitRemote
        gitRemote.configuration >> remoteConfiguration

        project.getExtensions() >> extensions
        project.logger >> logger
        extensions.getByName("kaytee") >> config
        extensions.extraProperties >> ext
        config.baseVersion = "1.2.3.4"
    }

    def "Check throws no exception when base version not in use already"() {
        given:
        ext.get(KayTeePlugin.KAYTEE_CONFIG_FLAG) >> true
        ext.get(KayTeePlugin.KAYTEE_COMMIT_ID) >> sha0
        List<Tag> tags = ImmutableList.of(newTag("2.2.2.2", commit1))
        gitLocal.tags() >> tags

        when:
        delegate.check()

        then:
        noExceptionThrown()
    }

    def "Check throws no exception when there are no existing tags"() {
        given:
        List<Tag> tags = ImmutableList.of()
        gitLocal.tags() >> tags

        when:
        delegate.check()

        then:
        noExceptionThrown()
    }

    def "Check throws no exception when base version is in use, but on current HEAD commit"() {

        given:
        List<Tag> tags = ImmutableList.of(newTag("1.2.3.4.aaaa", headCommit))
        gitLocal.tags() >> tags

        when:
        delegate.check()

        then:
        noExceptionThrown()
    }

    def "Check throws exception when base version found on a commit other than current HEAD"() {
        given:
        List<Tag> tags = ImmutableList.of(newTag("1.2.3.4.abaaaaa", commit1))
        gitLocal.tags() >> tags

        when:
        delegate.check()

        then:
        thrown GitLocalException
    }

    Tag newTag(String s, GitCommit commit) {

        return new Tag(s, ZonedDateTime.now(), ZonedDateTime.now(), personIdent, "who cares", commit, Tag.TagType.ANNOTATED)
    }

}
