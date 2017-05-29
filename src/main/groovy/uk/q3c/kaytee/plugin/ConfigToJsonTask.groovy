package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Creates a properties file containing build information and writes it to the build directory, in folder 'main/resources'
 * This ensures it is included in the jar
 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class ConfigToJsonTask extends DefaultTask {

    final ConfigToJsonTaskDelegate delegate

    ConfigToJsonTask() {
        delegate = new ConfigToJsonTaskDelegate(project)
    }

    @TaskAction
    void writeInfo() throws IOException {
        try {
            delegate.writeInfo()
        }
        catch (Exception e) {
            final String msg = "Failed to create KayTee config json file"
            getLogger().lifecycle(msg, e)
            throw new GradleException(msg, e)
        }
    }


}
