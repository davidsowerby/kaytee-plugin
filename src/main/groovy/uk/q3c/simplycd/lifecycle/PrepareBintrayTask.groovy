package uk.q3c.simplycd.lifecycle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 *
 * Transfers relevant configuration from {@link SimplyCDProjectExtension} to the Bintray configuration.
 *
 * This task should is only invoked by SimplyCD if SimplyCDProjectExtension.release.toBintray is true
 *
 * Created by David Sowerby on 20 May 2017
 */
class PrepareBintrayTask extends DefaultTask {

    PrepareBintrayDelegateTask delegate

    PrepareBintrayTask() {
        this.delegate = new PrepareBintrayDelegateTask(project)
    }

    @TaskAction
    void prepare() {
        try {
            delegate.prepare()
        } catch (Exception e) {
            final String msg = "Unable to prepare the Bintray configuration"
            getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)
        }
    }
}
