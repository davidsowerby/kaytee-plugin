package uk.q3c.simplycd.lifecycle

import org.gradle.util.ConfigureUtil

/**
 *
 * Properties relating to the project from a SimplyCD perspective.
 *
 * <b>NOTE:</b> if you add / delete a property MAKE SURE YOU CHANGE the copy method
 *
 * Created by David Sowerby on 19 Jan 2017
 */

class SimplyCDProjectExtension {

    String remoteRepoUserName = "davidsowerby";


    class GroupConfig {
        boolean enabled = false
        boolean qualityGate = false
        boolean auto = true
        boolean manual = false
        boolean external = false
        String externalRepoUrl = ""
        String externalRepoTask = "test"
    }

    class UnitTestConfig extends GroupConfig {
        UnitTestConfig() {
            super.setEnabled(true)
        }
    }

    UnitTestConfig unitTest = new UnitTestConfig()
    GroupConfig integrationTest = new GroupConfig()
    GroupConfig functionalTest = new GroupConfig()
    GroupConfig acceptanceTest = new GroupConfig()
    GroupConfig smokeTest = new GroupConfig()

    def unitTest(Closure closure) {
        ConfigureUtil.configure(closure, unitTest)
    }

    def integrationTest(Closure closure) {
        ConfigureUtil.configure(closure, integrationTest)
    }

    def functionalTest(Closure closure) {
        ConfigureUtil.configure(closure, functionalTest)
    }

    def acceptanceTest(Closure closure) {
        ConfigureUtil.configure(closure, acceptanceTest)
    }

    def smokeTest(Closure closure) {
        ConfigureUtil.configure(closure, smokeTest)
    }

    /**
     * This seems unnecessary, but the extension is byte enhanced by Gradle, which causes Jackson to blow up (stack overflow)
     * This just copies out the data into an un-enhanced instance
     *
     * @param config
     * @return
     */
    SimplyCDProjectExtension copy() {
        SimplyCDProjectExtension properConfig = new SimplyCDProjectExtension()

        return properConfig
    }

}
