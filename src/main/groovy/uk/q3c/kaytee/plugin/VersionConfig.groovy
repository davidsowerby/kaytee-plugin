package uk.q3c.kaytee.plugin

/**
 * Created by David Sowerby on 20 May 2017
 */
class ReleaseConfig {

    boolean toBintray = true
    boolean mergeToMaster = true
    boolean versionTag = true

    ReleaseConfig(ReleaseConfig other) {
        this.toBintray = other.toBintray
        this.mergeToMaster = other.mergeToMaster
        this.versionTag = other.versionTag
    }

    ReleaseConfig() {
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ReleaseConfig that = (ReleaseConfig) o

        if (mergeToMaster != that.mergeToMaster) return false
        if (toBintray != that.toBintray) return false
        if (versionTag != that.versionTag) return false

        return true
    }

    int hashCode() {
        int result
        result = (toBintray ? 1 : 0)
        result = 31 * result + (mergeToMaster ? 1 : 0)
        result = 31 * result + (versionTag ? 1 : 0)
        return result
    }
}
