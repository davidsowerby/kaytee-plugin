package uk.q3c.kaytee.plugin

/**
 * Created by David Sowerby on 23 Apr 2017
 */
class GroupConfig {

    boolean enabled = false
    boolean qualityGate = false
    boolean auto = true
    boolean manual = false
    boolean external = false
    String externalRepoUrl = ""
    String externalRepoTask = "test"
    TestGroupThresholds thresholds = new TestGroupThresholds()

    GroupConfig() {
    }


    GroupConfig(GroupConfig other) {
        enabled = other.enabled
        qualityGate = other.qualityGate
        auto = other.auto
        manual = other.manual
        external = other.external
        externalRepoUrl = other.externalRepoUrl
        externalRepoTask = other.externalRepoTask
        thresholds = new TestGroupThresholds(other.thresholds)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GroupConfig that = (GroupConfig) o

        if (auto != that.auto) return false
        if (enabled != that.enabled) return false
        if (external != that.external) return false
        if (manual != that.manual) return false
        if (qualityGate != that.qualityGate) return false
        if (externalRepoTask != that.externalRepoTask) return false
        if (externalRepoUrl != that.externalRepoUrl) return false
        if (thresholds != that.thresholds) return false

        return true
    }

    int hashCode() {
        int result
        result = (enabled ? 1 : 0)
        result = 31 * result + (qualityGate ? 1 : 0)
        result = 31 * result + (auto ? 1 : 0)
        result = 31 * result + (manual ? 1 : 0)
        result = 31 * result + (external ? 1 : 0)
        result = 31 * result + externalRepoUrl.hashCode()
        result = 31 * result + externalRepoTask.hashCode()
        result = 31 * result + thresholds.hashCode()
        return result
    }
}
