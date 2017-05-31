package uk.q3c.kaytee.plugin

/**
 * Created by David Sowerby on 20 May 2017
 */
class ReleaseConfig {

    boolean toBintray = true
    boolean mergeToMaster = true

    ReleaseConfig(ReleaseConfig other) {
        this.toBintray = other.toBintray
        this.mergeToMaster = other.mergeToMaster
    }

    ReleaseConfig() {
    }
}
