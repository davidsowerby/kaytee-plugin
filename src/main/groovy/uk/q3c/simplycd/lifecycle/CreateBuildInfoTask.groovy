package uk.q3c.simplycd.lifecycle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
/**
 * Creates a properties file containing build information and writes it to the build directory, in folder 'main/resources'
 * This ensures it is included in the jar.  Also tags the commit being built, or confirms that the tag already exists
 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class CreateBuildInfoTask extends DefaultTask {

    CreateBuildInfoTaskDelegate delegate

    CreateBuildInfoTask() {
        delegate = new CreateBuildInfoTaskDelegate(project)
    }

    @TaskAction
    void writeInfo() throws IOException {
        project.logger.debug("writing build info")
        try {
            delegate.writeInfo()
        }
        catch (Exception e) {
            final String msg = "Failed to create build info file or failed to tag the version (or both)"
            getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)
        } finally {
            if (delegate.gitPlus != null) {
                delegate.gitPlus.close()
            }
        }

    }


}
