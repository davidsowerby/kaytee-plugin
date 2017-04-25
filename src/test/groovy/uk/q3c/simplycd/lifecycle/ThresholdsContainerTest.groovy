package uk.q3c.simplycd.lifecycle

import org.gradle.internal.reflect.Instantiator

/**
 * Created by David Sowerby on 24 Apr 2017
 */
class ThresholdsContainerTest extends JsonTest {


    ThresholdsContainer container
    Instantiator instantiator = Mock(Instantiator)

    def setup() {
        container = new ThresholdsContainer(instantiator)
    }

    def "copy"() {
        when:
        TestGroupThresholds group1 = container.create("acceptanceTest")
        group1.method = 53.3
        group1.complexity = 99.9
        resource = container


        then:
        validateRoundTrip(ThresholdsContainer.class)
    }
}
