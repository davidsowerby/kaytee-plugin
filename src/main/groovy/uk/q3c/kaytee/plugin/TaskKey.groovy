package uk.q3c.kaytee.plugin

import com.google.common.collect.ImmutableList
import uk.q3c.krail.i18n.I18NKey

import static uk.q3c.kaytee.plugin.TaskNames.*

/**
 * Created by David Sowerby on 08 Jun 2017
 */
enum TaskKey implements I18NKey {
    Custom(CUSTOM),
    Version_Check(VERSION_CHECK),
    Extract_Gradle_Configuration(EXTRACT_CONFIG_TASK_NAME),
    Unit_Test(UNIT_TEST),
    Integration_Test(INTEGRATION_TEST),
    Generate_Build_Info(GENERATE_BUILD_INFO_TASK_NAME),
    Generate_Change_Log(GENERATE_CHANGE_LOG_TASK_NAME),
    Publish_to_Local(PUBLISH_TO_LOCAL),
    Functional_Test(FUNCTIONAL_TEST),
    Acceptance_Test(ACCEPTANCE_TEST),
    Merge_to_Master(MERGE_TO_MASTER),
    Bintray_Upload(BINTRAY_UPLOAD),
    Production_Test(PRODUCTION_TEST),
    Tag(TAG)

    private String gradleTask

    TaskKey(String gradleTask) {
        this.gradleTask = gradleTask
    }

    static List<TaskKey> testTasks = ImmutableList.of(Unit_Test, Integration_Test, Functional_Test, Acceptance_Test, Production_Test)

    QualityGateTaskKey qualityGateTaskKey() {
        if (!testTasks.contains(this)) {
            throw new IllegalArgumentException("Only test tasks have a quality gate")
        }
        switch (this) {
            case Unit_Test: return QualityGateTaskKey.Unit_Test_Quality_Gate
            case Integration_Test: return QualityGateTaskKey.Integration_Test_Quality_Gate
            case Functional_Test: return QualityGateTaskKey.Functional_Test_Quality_Gate
            case Acceptance_Test: return QualityGateTaskKey.Acceptance_Test_Quality_Gate
            case Production_Test: return QualityGateTaskKey.Production_Test_Quality_Gate
        }
        return QualityGateTaskKey.Unit_Test_Quality_Gate // should never actually happen
    }

    String qualityGateGradleTask() {
        return qualityGateTaskKey().gradleTask()
    }

    String gradleTask() {
        return gradleTask
    }

    static TaskKey fromGradleTask(String gradleTask) {
        for (TaskKey key : values()) {
            if (gradleTask == key.gradleTask()) {
                return key
            }
        }
        throw new IllegalArgumentException("'$gradleTask' is not a defined Gradle task")
    }

    boolean isTestKey() {
        return testTasks.contains(this)
    }


}