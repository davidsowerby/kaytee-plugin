package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTaskDelegate {

    @SuppressWarnings("GrMethodMayBeStatic")
    void generate(Project project, ChangeLog changelog) {
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        changelog.configuration.copyFrom(config.changeLog)
        writeOutConfig(project)
        changelog.generate()
    }

    /**
     * Writes config to file if logger is debug enabled
     *
     * @param project
     */
    private void writeOutConfig(Project project) {
        Logger logger = project.getLogger()

        if (logger.isDebugEnabled()) {
            String configFileName = "changeLogConfig.json"
            String thresholdsFileName = "changeLogThresholds.json"
            new ConfigWriter().writeOutConfig(project, configFileName, thresholdsFileName)

            new ConfigWriter().writeOutChangeLogConfig(project)
        }
    }


}
