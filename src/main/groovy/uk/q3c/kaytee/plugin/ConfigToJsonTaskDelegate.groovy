package uk.q3c.kaytee.plugin

import org.gradle.api.Project

/**
 * Carries out most of the work for {@link uk.q3c.kaytee.plugin.CreateBuildInfoTask}, to enable testing

 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class ConfigToJsonTaskDelegate {

    final Project project

    ConfigToJsonTaskDelegate(Project project) {
        this.project = project
    }


    void writeInfo() throws IOException {
        logLifecycle("generating KayTee config in kaytee.json in build dir")
        new ConfigWriter().writeOutConfig(project, "kaytee.json")
    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg)
    }


}
