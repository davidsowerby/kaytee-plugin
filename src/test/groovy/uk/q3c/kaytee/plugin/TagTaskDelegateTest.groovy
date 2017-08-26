package uk.q3c.kaytee.plugin

import org.gradle.api.plugins.ExtensionContainer
import uk.q3c.util.version.VersionNumber

/**
 * Created by David Sowerby on 06 Dec 2016
 */
class TagTaskDelegateTest extends TestWithGitPlus {

    TagTaskDelegate delegate
    ExtensionContainer projectExtensions = Mock(ExtensionContainer)

    def setup() {
        delegate = new TagTaskDelegate(project, gitPlus)
    }

    def "no tag if a rerun"() {
        given:
        ext.get(KayTeePlugin.KAYTEE_RERUN) >> true

        when:
        delegate.tagHead()

        then:
        0 * gitLocal.tag(_, _)
    }

    def "calls tag with version as name"() {
        given:
        project.version >> new VersionNumber(1, 2, 3, 4)
        ext.get(KayTeePlugin.KAYTEE_RERUN) >> false

        when:
        delegate.tagHead()

        then:
        1 * gitLocal.tag("1.2.3.4", _)
        1 * gitLocal.push(true, false)

    }




}
