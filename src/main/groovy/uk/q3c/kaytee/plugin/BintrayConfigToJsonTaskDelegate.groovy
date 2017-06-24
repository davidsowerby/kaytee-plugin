package uk.q3c.kaytee.plugin

import com.jfrog.bintray.gradle.BintrayExtension
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by David Sowerby on 24 Jun 2017
 */
class BintrayConfigToJsonTaskDelegate {

    void writeConfig(Project project) {
        if (!project.buildDir.exists()) {
            project.logger.debug("Creating build directory, probably preceded by 'clean' task")
            FileUtils.forceMkdir(project.buildDir)
        }
        BintrayExtension bintray = project.extensions.getByName("bintray") as BintrayExtension
        File bintrayConfigFile = new File(project.buildDir, "bintrayConfig.json")
        BintrayConfigWrapper bintrayConfigWrapper = new BintrayConfigWrapper(bintray)
        FileUtils.writeStringToFile(bintrayConfigFile, bintrayConfigWrapper.toString())
    }
}
