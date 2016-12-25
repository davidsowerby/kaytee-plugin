package uk.q3c.simplycd.lifecycle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.q3c.build.changelog.ChangeLog
import uk.q3c.build.changelog.ChangeLogFactory

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTask extends DefaultTask {


    @TaskAction
    public void generate() {
        def config = project.simplycd as SimplyCDContainer

        ChangeLog changelog = ChangeLogFactory.instance
        changelog.remoteRepoUser = config.getRemoteRepoUserName()
        changelog.projectDirParent = project.getProjectDir().getParentFile()
        changelog.projectName = project.getName()
        changelog.generate()


    }


}
