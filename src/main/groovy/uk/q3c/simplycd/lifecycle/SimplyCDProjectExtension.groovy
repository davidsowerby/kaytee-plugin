package uk.q3c.simplycd.lifecycle
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

    Boolean test = true
    Boolean testQualityGate = true
    Boolean integrationTest = false
    Boolean integrationTestQualityGate = false
    Boolean functionalTest = false
    Boolean functionalTestQualityGate = false
    Boolean acceptanceTest = false
    Boolean acceptanceTestQualityGate = false
    Boolean smokeTest = false
    Boolean smokeTestQualityGate = false


    boolean testEnabled(String group) {
        switch (group) {
            case 'test': return test
            case 'integrationTest': return integrationTest
            case 'functionalTest': return functionalTest
            case 'acceptanceTest': return acceptanceTest
            case 'smokeTest': return smokeTest
        }
    }

    boolean qualityGateEnabled(String group) {
        switch (group) {
            case 'test': return testQualityGate
            case 'integrationTest': return integrationTestQualityGate
            case 'functionalTest': return functionalTestQualityGate
            case 'acceptanceTest': return acceptanceTestQualityGate
            case 'smokeTest': return smokeTestQualityGate
        }
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
        properConfig.remoteRepoUserName = this.remoteRepoUserName
        properConfig.testQualityGate = this.testQualityGate
        properConfig.integrationTest = this.integrationTest
        properConfig.integrationTestQualityGate = this.integrationTestQualityGate
        properConfig.functionalTest = this.functionalTest
        properConfig.functionalTestQualityGate = this.functionalTestQualityGate
        properConfig.acceptanceTest = this.acceptanceTest
        properConfig.acceptanceTestQualityGate = this.acceptanceTestQualityGate
        properConfig.smokeTest = this.smokeTest
        properConfig.smokeTestQualityGate = this.smokeTestQualityGate
        return properConfig
    }

}
