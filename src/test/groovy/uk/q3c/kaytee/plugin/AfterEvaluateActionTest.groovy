package uk.q3c.kaytee.plugin

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by David Sowerby on 27 May 2017
 */
class AfterEvaluateActionTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder
    File temp
    Project project = GroovyMock(Project)
    AfterEvaluateAction action
    Logger logger = Mock(Logger)
    ExtensionContainer extensions = Mock(ExtensionContainer)
    KayTeeExtension config = new KayTeeExtension()
    BintrayExtension bintrayExtension
    KayTeeVersion version = Mock(KayTeeVersion)

    def setup() {
        temp = temporaryFolder.getRoot()
        version.toString() >> "1.2.3.4.aaaaa"
        action = new AfterEvaluateAction(project)
        project.logger >> logger
        project.extensions >> extensions
        extensions.getByName('kaytee') >> config
        project.name >> 'wiggly'
        project.projectDir >> new File(temp, "wiggly")
        project.bintrayKey >> "xxyy55"
        project.version >> version
        bintrayExtension = new BintrayExtension(project)
        extensions.getByName('bintray') >> bintrayExtension

        // These are normally set during Plugin execute
        config.gitLocalConfiguration.projectName = project.name
        config.gitLocalConfiguration.projectDirParent = temp

        config.gitRemoteConfiguration.repoUser = 'davidsowerby'
        config.gitRemoteConfiguration.repoName = 'wiggly'
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "execute"() {

        when:
        action.execute(project)

        then:
        config.wikiLocalConfiguration.active == true
        config.wikiLocalConfiguration.cloneFromRemote == false
        config.wikiLocalConfiguration.projectName == project.name

        config.gitRemoteConfiguration.repoName == project.name
        config.gitRemoteConfiguration.repoUser == config.remoteRepoUserName

        config.gitLocalConfiguration.cloneFromRemote == false

        config.changeLog.projectName == project.name
        config.changeLog.projectDirParent == project.projectDir.parentFile
        config.changeLog.autoTagLatestCommit == false
        config.changeLog.remoteRepoUser == config.remoteRepoUserName

        bintrayExtension.pkg.name == project.name
        bintrayExtension.pkg.repo == 'maven'
        bintrayExtension.key == project.bintrayKey
        bintrayExtension.pkg.websiteUrl == "https://github.com/davidsowerby/wiggly"
        bintrayExtension.pkg.issueTrackerUrl == "https://github.com/davidsowerby/wiggly/issues/"
        bintrayExtension.pkg.vcsUrl == "https://github.com/davidsowerby/wiggly.git"
        bintrayExtension.pkg.version.released != null
        bintrayExtension.pkg.version.name == "1.2.3.4.aaaaa"

    }
}
