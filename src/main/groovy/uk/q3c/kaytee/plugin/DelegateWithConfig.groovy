package uk.q3c.kaytee.plugin

import org.gradle.api.Project

/**
 * Created by David Sowerby on 24 May 2017
 */
class DelegateWithConfig {
    final Project project
    KayTeeExtension config

    DelegateWithConfig(Project project) {
        this.project = project
    }

    protected void prepare() {
        project.logger.debug("retrieving config for ${this.getClass().getName()}")
        config = project.extensions.getByName("kaytee") as KayTeeExtension
    }

    protected void logDebug(String msg) {
        project.logger.debug(msg)
    }

    protected void logLifecycle(String msg) {
        project.logger.debug(msg)
    }

    protected void logLifecycle(String msg, Exception e) {
        project.logger.debug(msg, e)
    }
}
