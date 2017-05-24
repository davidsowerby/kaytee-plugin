package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project

/**
 * Created by David Sowerby on 24 May 2017
 */
class DelegateWithConfig {
    final Project project
    SimplyCDProjectExtension config

    DelegateWithConfig(Project project) {
        this.project = project
    }

    protected void prepare() {
        project.logger.debug("retrieving config")
        config = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
    }
}
