package uk.q3c.simplycd.lifecycle

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import spock.lang.Specification

/**
 * Created by David Sowerby on 24 May 2017
 */
class PrepareBintrayDelegateTaskTest extends Specification {

    Project project = Mock(Project)
    BintrayExtension extension

    def setup() {
        extension = new BintrayExtension(project)
    }

    def "Name"() {
        given:
        println extension
        expect: false
    }
}
