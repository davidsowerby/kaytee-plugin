package uk.q3c.simplycd.lifecycle

import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import uk.q3c.build.gitplus.GitSHA
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.DefaultGitLocalConfiguration
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration
import uk.q3c.build.gitplus.remote.GitRemote
import uk.q3c.build.gitplus.remote.GitRemoteConfiguration

/**
 * Created by David Sowerby on 06 Dec 2016
 */
class CreateBuildInfoTaskTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    CreateBuildInfoTask task
    GitPlus gitPlus = Mock(GitPlus)
    GitLocal gitLocal = Mock(GitLocal)
    GitLocalConfiguration localConfiguration
    GitRemote gitRemote = Mock(GitRemote)
    GitRemoteConfiguration remoteConfiguration = Mock(GitRemoteConfiguration)
    GitSHA gitSHA
    SimplyCDVersion versionObject = Mock(SimplyCDVersion)

    def setup() {
        gitSHA = testSha()
        gitRemote.configuration >> remoteConfiguration
        localConfiguration = new DefaultGitLocalConfiguration()
        gitPlus.local >> gitLocal
        gitPlus.remote >> gitRemote
        gitLocal.localConfiguration >> localConfiguration
        temp = temporaryFolder.getRoot()
        Project project = ProjectBuilder.builder().build()
        versionObject.toString() >> '9.9.9.1000'
        project.version = versionObject
        task = project.getTasks().create("buildInfo", CreateBuildInfoTask.class);
        task.delegate.gitPlus = gitPlus
    }

    def "Write build info"() {
        when:
        task.writeInfo()


        then:
        1 * remoteConfiguration.active(false)
        1 * gitPlus.execute()
        1 * gitLocal.tag('9.9.9.1000', 'version 9.9.9.1000')
        1 * gitLocal.push(true, false)
        1 * gitLocal.currentBranch() >> new GitBranch('develop')
        1 * gitLocal.latestCommitSHA(_) >> gitSHA
    }

    private GitSHA testSha() {
        return new GitSHA(DigestUtils.sha1Hex('42'))
    }


}
