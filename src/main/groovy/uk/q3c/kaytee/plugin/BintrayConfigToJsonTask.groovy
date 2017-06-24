package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by David Sowerby on 24 Jun 2017
 */
class BintrayConfigToJsonTask extends DefaultTask {

    BintrayConfigToJsonTaskDelegate delegate

    BintrayConfigToJsonTask() {
        delegate = new BintrayConfigToJsonTaskDelegate()
    }

    @TaskAction
    void writeConfig() {
        delegate.writeConfig(project)
    }
}
