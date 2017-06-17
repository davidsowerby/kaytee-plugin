package uk.q3c.kaytee.plugin

import org.gradle.util.ConfigureUtil

/**
 * Created by David Sowerby on 23 Apr 2017
 */
class GroupConfig {

    boolean enabled = false
    boolean qualityGate = false
    boolean auto = true
    boolean manual = false
    boolean delegated = false
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
        auto = other.auto
        manual = other.manual
        delegated = other.delegated
        thresholds = new TestGroupThresholds(other.thresholds)
        delegate = new DelegateProjectConfig(other.delegate)
    }

    def validate(TaskKey group, List<String> errors) {
        if (delegated) {
            delegate.validate(group, errors)
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GroupConfig that = (GroupConfig) o

        if (auto != that.auto) return false
        if (enabled != that.enabled) return false
        if (delegated != that.delegated) return false
        if (manual != that.manual) return false
        if (qualityGate != that.qualityGate) return false
        if (thresholds != that.thresholds) return false
        if (delegate != that.delegate) return false


        return true
    }

    int hashCode() {
        int result
        result = (enabled ? 1 : 0)
        result = 31 * result + (qualityGate ? 1 : 0)
        result = 31 * result + (auto ? 1 : 0)
        result = 31 * result + (manual ? 1 : 0)
        result = 31 * result + (delegated ? 1 : 0)
        result = 31 * result + thresholds.hashCode()
        result = 31 * result + delegate.hashCode()
        return result
    }
}
