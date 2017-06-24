package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Created by David Sowerby on 16 Jun 2017
 */
class TagTask extends DefaultTask {

    TagTaskDelegate delegate

    TagTask() {
        delegate = new TagTaskDelegate(project)
    }

    @TaskAction
    String tagHead() {
        try {
            logger.debug("Running tag task")
            return delegate.tagHead()
        }
        catch (Exception e) {
            throw new GradleException("Failed to apply tag", e)
        } finally {
            delegate.gitPlus.close()
        }
    }
}
