package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Created by David Sowerby on 13 Jun 2017
 */
class SetVersionTask extends DefaultTask {

    SetVersionTaskDelegate delegate

    SetVersionTask() {
        this.delegate = new SetVersionTaskDelegate(project)
    }

    @TaskAction
    void check() {
        try {
            delegate.check()
        } catch (Exception e) {
            final String msg = "Version check failed"
            getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)
        }

    }
}
