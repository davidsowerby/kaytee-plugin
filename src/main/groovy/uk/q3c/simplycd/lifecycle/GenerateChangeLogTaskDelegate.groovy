package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTaskDelegate {

    void generate(Project project, ChangeLog changelog) {

        logConfig(project, changelog)
        SimplyCDProjectExtension config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        changelog.configuration = config.changeLog
        changelog.generate()
    }

    private void logConfig(Project project, ChangeLog changelog) {
        Logger logger = project.getLogger()
        if (logger.isDebugEnabled()) {
            StringBuilder buf = new StringBuilder("Generating change log with following config:\n")
            buf.append("destination file: ${changelog.outputFile()}\n")
            buf.append("wikiLocal is active: ${changelog.gitPlus().wikiLocal.active}\n")
            buf.append("wikiLocal create is: ${changelog.gitPlus().wikiLocal.create}\n")
            buf.append("wikiLocal clone is: ${changelog.gitPlus().wikiLocal.cloneFromRemote}\n")
            logger.log(LogLevel.DEBUG, buf.toString())
        }

    }

}
