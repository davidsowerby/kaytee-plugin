package uk.q3c.simplycd.lifecycle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.q3c.build.changelog.ChangeLogFactory

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class GenerateChangeLogTask extends DefaultTask {

    GenerateChangeLogTaskDelegate delegate

    GenerateChangeLogTask() {
        delegate = new GenerateChangeLogTaskDelegate()
    }

    @TaskAction
    void generate() {
        delegate.generate(project, ChangeLogFactory.instance)
    }


}
