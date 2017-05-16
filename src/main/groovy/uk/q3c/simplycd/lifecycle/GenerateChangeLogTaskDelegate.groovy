package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTaskDelegate {

    void generate(Project project, ChangeLog changelog) {
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        logConfig(project, config)
        changelog.configuration = config.changeLog
        changelog.generate()
    }

    private void logConfig(Project project, SimplyCDProjectExtension config) {
        Logger logger = project.getLogger()
        if (logger.isDebugEnabled()) {
            ObjectMapper objectMapper = new ObjectMapper()
            StringWriter sw = new StringWriter()
            objectMapper.writeValue(sw, config)
            logger.log(LogLevel.DEBUG, sw.toString())
        }

    }

}
