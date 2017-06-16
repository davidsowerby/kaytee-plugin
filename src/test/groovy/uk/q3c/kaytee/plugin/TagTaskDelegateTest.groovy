package uk.q3c.kaytee.plugin

import org.gradle.api.plugins.ExtensionContainer
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
        ext.get(KayTeePlugin.KAYTEE_RERUN) >> false

        when:
        delegate.tagHead()

        then:
        1 * gitLocal.tag(_, _)

    }




}
