package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project

/**
 * Created by David Sowerby on 20 Dec 2016
 */
class SimplyCDVersion {

    static String VERSION_PROPERTY = 'SIMPLYCD_BUILD_NUMBER'
    final Project project

    SimplyCDVersion(Project project) {
        this.project = project
    }

    @Override
    String toString() {
        File userHome = new File(System.getProperty('user.home'))
        File simplyCdHome = new File(userHome, 'simplycd')
        File simplyCdProjects = new File(simplyCdHome, 'projects')
        File buildNumberFile = new File(simplyCdProjects, project.name + '.properties')


        String buildNumber
        if (!buildNumberFile.exists()) {
            buildNumber = 'dev'
        } else {
            Properties buildProperties = new Properties()
            try {
                buildProperties.load(new FileReader(buildNumberFile))
                buildNumber = buildProperties.getProperty('buildNumber', 'dev')
            } catch (Exception e) {
                project.logger.warn('Unable to load build number properties file', e)
                buildNumber = 'dev'
            }
        }
        return project.baseVersion + '.' + buildNumber
    }
}
