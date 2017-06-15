package uk.q3c.kaytee.plugin

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
        delegate = new CreateBuildInfoTaskDelegate(project, new VersionCheckTaskDelegate(project))
    }

    @TaskAction
    void writeInfo() throws IOException {
        project.logger.debug("writing build info")
        try {
            delegate.writeInfo()
        }
        catch (Exception e) {
            String detailMsg = "not known"
            if (e.message != null) {
                detailMsg = e.message
            }
            final String msg = "Failed to create build info file, reason: $detailMsg"
            getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)
        }

    }


}
