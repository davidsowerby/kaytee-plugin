package uk.q3c.kaytee.plugin

import static uk.q3c.kaytee.plugin.TaskNames.*

/**
 * Created by David Sowerby on 08 Jun 2017
 */
enum QualityGateTaskKey {
    Acceptance_Test_Quality_Gate(ACCEPTANCE_QUALITY_GATE),
    Integration_Test_Quality_Gate(INTEGRATION_QUALITY_GATE),
    Functional_Test_Quality_Gate(FUNCTIONAL_QUALITY_GATE),
    Production_Test_Quality_Gate(PRODUCTION_QUALITY_GATE),
    Unit_Test_Quality_Gate(UNIT_TEST_QUALITY_GATE)

    private String gradleTask

    QualityGateTaskKey(String gradleTask) {
        this.gradleTask = gradleTask
    }

    String gradleTask() {
        return this.gradleTask
    }
}