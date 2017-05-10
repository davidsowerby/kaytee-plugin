package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import uk.q3c.build.gitplus.GitSHA

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class SimplyCDVersion {


    final Project project

    SimplyCDVersion(Project project) {
        this.project = project
    }

    @Override
    String toString() {
        File propertiesFile = new File(project.getBuildDir(), CreateBuildInfoTaskDelegate.PATH_TO_BUILD_INFO_PROPS)

        String buildNumber = 'dev'
        if (propertiesFile.exists()) {
            Properties buildProperties = new Properties()
            try {
                buildProperties.load(new FileReader(propertiesFile))
                String commitId = buildProperties.getProperty(CreateBuildInfoTaskDelegate.PROPERTY_NAME_COMMIT_ID)
                buildNumber = new GitSHA(commitId).short()
            } catch (Exception e) {
                // do nothing, just leave build number as 'dev'
                project.logger.warn('Unable to load build info properties file at ' + propertiesFile.getAbsolutePath())
            }
        } else {
            project.logger.warn('Build info properties file is missing, expected at: ' + propertiesFile.getAbsolutePath())
        }
        return project.property("baseVersion") + '.' + buildNumber
    }
}
