package uk.q3c.kaytee.plugin

import uk.q3c.util.version.Scheme

/**
 * Created by David Sowerby on 20 May 2017
 */
class VersionConfig {

    String number = '0.0.0.0'
    String qualifier = ""
    String buildMetaData = ""
    Scheme scheme = new Scheme()

    VersionConfig(VersionConfig other) {
        this.number = other.number
        this.qualifier = other.qualifier
        this.buildMetaData = other.buildMetaData
        this.scheme = other.scheme
    }

    VersionConfig() {
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        VersionConfig that = (VersionConfig) o

        if (buildMetaData != that.buildMetaData) return false
        if (number != that.number) return false
        if (qualifier != that.qualifier) return false
        if (scheme != that.scheme) return false

        return true
    }

    int hashCode() {
        int result
        result = number.hashCode()
        result = 31 * result + qualifier.hashCode()
        result = 31 * result + buildMetaData.hashCode()
        result = 31 * result + scheme.hashCode()
        return result
    }
}
