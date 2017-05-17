package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
/**
 * Carries out most of the work for {@link CreateBuildInfoTask}, to enable testing

 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class ConfigToJsonTaskDelegate {

    final Project project

    ConfigToJsonTaskDelegate(Project project) {
        this.project = project
    }


    void writeInfo() throws IOException {
        logLifecycle("generating SimplyCD config in simplycd.json and thresholds.json in build dir")
        new ConfigWriter().writeOutConfig(project, "simplycd.json", "thresholds.json")
    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg)
    }


}
