package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Created by David Sowerby on 20 May 2017
 */
class MergeToMasterTask extends DefaultTask {

    final MergeToMasterDelegateTask delegate

    MergeToMasterTask() {
        delegate = new MergeToMasterDelegateTask(project)
    }

    @TaskAction
    void merge() {
        try {
            delegate.merge()
        }
        catch (Exception e) {
            final String msg = "Merge to master branch failed"
            project.getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)

        } finally {
            if (delegate.gitPlus != null) {
                delegate.gitPlus.close()
            }
        }
    }
}
