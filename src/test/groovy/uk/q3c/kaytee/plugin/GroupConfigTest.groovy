package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import static uk.q3c.kaytee.plugin.TaskType.DELEGATED

/**
 * Created by David Sowerby on 23 Jul 2017
 */
class GroupConfigTest extends Specification {

    GroupConfig config

    def setup() {
        config = new GroupConfig()
    }

    def "copy constructor and JSON round robin"() {
        given:
        config.taskType = DELEGATED
        config.enabled = true

        ObjectMapper objectMapper = new ObjectMapper()
        StringWriter sw = new StringWriter()

        when:
        objectMapper.writeValue(sw, config)
        GroupConfig config2 = new GroupConfig(config)
        GroupConfig config3 = objectMapper.readValue(sw.toString(), GroupConfig)

        then:
        config2 == config
        config3 == config
    }


}
