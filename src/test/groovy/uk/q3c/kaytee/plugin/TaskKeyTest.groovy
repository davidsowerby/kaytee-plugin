package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableList
import spock.lang.Specification

import static uk.q3c.kaytee.plugin.QualityGateTaskKey.*
import static uk.q3c.kaytee.plugin.TaskKey.*
import static uk.q3c.kaytee.plugin.TaskNames.INTEGRATION_QUALITY_GATE

/**
 * Created by David Sowerby on 08 Jun 2017
 */
class TaskKeyTest extends Specification {

    static class TestObject {
        String s = "random"
        TaskKey key1 = Integration_Test
        QualityGateTaskKey key2 = Functional_Test_Quality_Gate
    }

    def "lookups"() {
        expect:
        Generate_Change_Log.gradleTask() == TaskNames.GENERATE_CHANGE_LOG_TASK_NAME
        testTasks() == ImmutableList.of(Unit_Test, Integration_Test, Functional_Test, Acceptance_Test, Production_Test)
        Unit_Test.qualityGateTaskKey() == Unit_Test_Quality_Gate
        Integration_Test.qualityGateTaskKey() == Integration_Test_Quality_Gate
        Functional_Test.qualityGateTaskKey() == Functional_Test_Quality_Gate
        Acceptance_Test.qualityGateTaskKey() == Acceptance_Test_Quality_Gate
        Production_Test.qualityGateTaskKey() == Production_Test_Quality_Gate

        Integration_Test.qualityGateTaskKey().gradleTask() == INTEGRATION_QUALITY_GATE
        Integration_Test.qualityGateGradleTask() == INTEGRATION_QUALITY_GATE
    }

    def "lookup quality gate with non test key"() {
        when:
        Merge_to_Master.qualityGateTaskKey()

        then:
        thrown IllegalArgumentException
    }

    def "Json round trip"() {
        given:
        TestObject obj = new TestObject()
        StringWriter sw = new StringWriter()
        ObjectMapper mapper = new ObjectMapper()
        TestObject obj2 = null

        when:
        mapper.writeValue(sw, obj)
        obj2 = mapper.readValue(sw.toString(), TestObject.class)

        then:
        obj2.key1 == Integration_Test
        obj2.key2 == Functional_Test_Quality_Gate
        obj2.s == "random"
    }

    def "TaskKey from gradle task"() {
        expect:
        fromGradleTask("test") == Unit_Test
        fromGradleTask("integrationTest") == Integration_Test
        fromGradleTask("functionalTest") == Functional_Test
    }
}
