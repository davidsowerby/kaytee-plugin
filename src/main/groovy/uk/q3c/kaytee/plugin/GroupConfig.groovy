package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.annotation.JsonIgnore
import org.gradle.util.ConfigureUtil

import static uk.q3c.kaytee.plugin.TaskType.*
/**
 * Created by David Sowerby on 23 Apr 2017
 */
class GroupConfig {

    boolean enabled = false
    boolean qualityGate = false
    TaskType taskType = GRADLE
    TestGroupThresholds thresholds = new TestGroupThresholds()
    DelegateProjectConfig delegate = new DelegateProjectConfig()

    GroupConfig() {
    }


    def thresholds(Closure closure) {
        ConfigureUtil.configure(closure, thresholds)
    }

    def delegate(Closure closure) {
        ConfigureUtil.configure(closure, delegate)
    }

    GroupConfig(GroupConfig other) {
        enabled = other.enabled
        qualityGate = other.qualityGate
        taskType = other.taskType
        thresholds = new TestGroupThresholds(other.thresholds)
        delegate = new DelegateProjectConfig(other.delegate)
    }

    def validate(TaskKey group, List<String> errors) {
        if (taskType == DELEGATED) {
            delegate.validate(group, errors)
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GroupConfig that = (GroupConfig) o

        if (taskType != that.taskType) return false
        if (enabled != that.enabled) return false
        if (qualityGate != that.qualityGate) return false
        if (thresholds != that.thresholds) return false
        if (delegate != that.delegate) return false


        return true
    }

    int hashCode() {
        int result
        result = (enabled ? 1 : 0)
        result = 31 * result + (qualityGate ? 1 : 0)
        result = 31 * result + (taskType ? 1 : 0)
        result = 31 * result + thresholds.hashCode()
        result = 31 * result + delegate.hashCode()
        return result
    }

    @JsonIgnore
    boolean isDelegated() {
        return taskType == DELEGATED
    }

    @JsonIgnore
    boolean isManual() {
        return taskType == MANUAL
    }

    @JsonIgnore
    boolean isGradle() {
        return taskType == GRADLE
    }
}
