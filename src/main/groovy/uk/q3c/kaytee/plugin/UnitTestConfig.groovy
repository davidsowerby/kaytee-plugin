package uk.q3c.kaytee.plugin
/**
 * Created by David Sowerby on 23 Apr 2017
 */
class UnitTestConfig extends GroupConfig {

    UnitTestConfig(UnitTestConfig other) {
        super(other)
    }

    UnitTestConfig() {
        setEnabled(true)
    }
}
