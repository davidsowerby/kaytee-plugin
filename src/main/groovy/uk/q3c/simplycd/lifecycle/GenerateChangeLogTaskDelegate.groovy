package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import uk.q3c.build.changelog.ChangeLog

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTaskDelegate {


    public void generate(Project project, SimplyCDContainer config, ChangeLog changelog) {

        changelog.remoteRepoUser(config.getRemoteRepoUserName())
        changelog.projectDirParent(project.getProjectDir().getParentFile())
        changelog.projectName(project.getName())
        changelog.generate()
    }


}
