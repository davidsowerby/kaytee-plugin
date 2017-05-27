package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * Writes config to JSON file
 */
@SuppressWarnings("GrMethodMayBeStatic")
class ConfigWriter {


    void writeOutConfig(Project project, String configFileName, String thresholdsFileName) {
        Logger logger = project.getLogger()
        SimplyCDProjectExtension jsonConfig = getExtension(project)
        ObjectMapper objectMapper = getObjectMapper()
        // build dir could have been removed by 'clean'
        if (!project.buildDir.exists()) {
            logger.debug("Creating build directory, probably preceded by 'clean' task")
            FileUtils.forceMkdir(project.buildDir)
        }

        File simplycdFile = new File(project.buildDir, configFileName)
        objectMapper.writeValue(simplycdFile, jsonConfig)
        logger.debug("Configuration written to " + simplycdFile)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    void writeOutChangeLogConfig(Project project) {
        Logger logger = project.getLogger()
        SimplyCDProjectExtension jsonConfig = getExtension(project)
        ObjectMapper objectMapper = getObjectMapper()

        // build dir could have been removed by 'clean'
        if (!project.buildDir.exists()) {
            logger.debug("Creating build directory, probably preceded by 'clean' task")
            FileUtils.forceMkdir(project.buildDir)
        }

        File configFile = new File(project.buildDir, "changeLogOnly.json")
        objectMapper.writeValue(configFile, jsonConfig.changeLog)
        logger.debug("ChangeLog configuration written to " + configFile)
    }

    private SimplyCDProjectExtension getExtension(Project project) {
        SimplyCDProjectExtension projectConfig = project.extensions.getByName('simplycd') as SimplyCDProjectExtension
        return projectConfig.copy()
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        return objectMapper
    }
}
