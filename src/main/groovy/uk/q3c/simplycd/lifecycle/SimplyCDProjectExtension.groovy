package uk.q3c.simplycd.lifecycle

import org.gradle.util.ConfigureUtil
import uk.q3c.build.changelog.DefaultChangeLogConfiguration
import uk.q3c.build.gitplus.local.DefaultGitLocalConfiguration
import uk.q3c.build.gitplus.remote.DefaultGitRemoteConfiguration

/**
 *
 * Properties relating to the project from a SimplyCD perspective.
 *
 * <b>NOTE:</b> if you add / delete a property MAKE SURE YOU CHANGE the copy constructor
 *
 * Created by David Sowerby on 19 Jan 2017
 */

class SimplyCDProjectExtension {

    SimplyCDProjectExtension() {
        changeLog = new DefaultChangeLogConfiguration()
        gitLocalConfiguration = new DefaultGitLocalConfiguration()
        wikiLocalConfiguration = new DefaultGitLocalConfiguration()
        gitRemoteConfiguration = new DefaultGitRemoteConfiguration()
    }

    SimplyCDProjectExtension(SimplyCDProjectExtension other) {

        this.remoteRepoUserName = other.remoteRepoUserName
        this.baseVersion = other.baseVersion

        unitTest = new UnitTestConfig(other.unitTest)
        integrationTest = new GroupConfig(other.integrationTest)
        functionalTest = new GroupConfig(other.functionalTest)
        acceptanceTest = new GroupConfig(other.acceptanceTest)
        productionTest = new GroupConfig(other.productionTest)

        String projectName = other.changeLog.projectName // for change log copy
        changeLog = ((other.changeLog) as DefaultChangeLogConfiguration).copy(projectName)

        projectName = other.gitLocalConfiguration.projectName
        gitLocalConfiguration = other.gitLocalConfiguration.copy(projectName)

        String user = other.gitRemoteConfiguration.repoUser
        String repoName = other.gitRemoteConfiguration.repoName
        gitRemoteConfiguration = other.gitRemoteConfiguration.copy(user, repoName)

        projectName = other.wikiLocalConfiguration.projectName
        wikiLocalConfiguration = other.wikiLocalConfiguration.copy(projectName)
    }
    String remoteRepoUserName = "davidsowerby"
    String baseVersion = '0.0.0.0'


    UnitTestConfig unitTest = new UnitTestConfig()
    GroupConfig integrationTest = new GroupConfig()
    GroupConfig functionalTest = new GroupConfig()
    GroupConfig acceptanceTest = new GroupConfig()
    GroupConfig productionTest = new GroupConfig()
    ReleaseConfig release = new ReleaseConfig()

    // Cannot use interface - Jackson does not know how to reconstruct it
    DefaultChangeLogConfiguration changeLog
    DefaultGitLocalConfiguration gitLocalConfiguration
    DefaultGitLocalConfiguration wikiLocalConfiguration
    DefaultGitRemoteConfiguration gitRemoteConfiguration

    def release(Closure closure) {
        ConfigureUtil.configure(closure, release)
    }

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

    def productionTest(Closure closure) {
        ConfigureUtil.configure(closure, productionTest)
    }

    def changelog(Closure closure) {
        ConfigureUtil.configure(closure, changeLog)
    }

    def gitLocal(Closure closure) {
        ConfigureUtil.configure(closure, gitLocalConfiguration)
    }

    def gitRemote(Closure closure) {
        ConfigureUtil.configure(closure, gitRemoteConfiguration)
    }

    def wikiLocal(Closure closure) {
        ConfigureUtil.configure(closure, wikiLocalConfiguration)
    }

    /**
     * This seems unnecessary, but the extension is byte enhanced by Gradle, which causes Jackson to blow up (stack overflow)
     * This just copies out the data into an un-enhanced instance, so Jackson does not get upset.
     *
     * You can just as easily use the copy constructor
     *
     * @param config
     * @return
     */
    SimplyCDProjectExtension copy() {
        return new SimplyCDProjectExtension(this)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SimplyCDProjectExtension that = (SimplyCDProjectExtension) o

        if (acceptanceTest != that.acceptanceTest) return false
        if (functionalTest != that.functionalTest) return false
        if (integrationTest != that.integrationTest) return false
        if (productionTest != that.productionTest) return false
        if (remoteRepoUserName != that.remoteRepoUserName) return false
        if (unitTest != that.unitTest) return false

        return true
    }

    int hashCode() {
        int result
        result = remoteRepoUserName.hashCode()
        result = 31 * result + unitTest.hashCode()
        result = 31 * result + integrationTest.hashCode()
        result = 31 * result + functionalTest.hashCode()
        result = 31 * result + acceptanceTest.hashCode()
        result = 31 * result + productionTest.hashCode()
        return result
    }
}
