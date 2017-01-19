package uk.q3c.simplycd.lifecycle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.apache.commons.io.FileUtils
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


    public void writeInfo() throws IOException {
        logLifecycle("generating SimplyCD config in simplycd.json and thresholds.json in build dir");
        SimplyCDProjectExtension projectConfig = project.extensions.getByName('simplycd') as SimplyCDProjectExtension
        SimplyCDProjectExtension jsonConfig = projectConfig.copy()


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)

        // build dir could have been removed by 'clean'
        if (!project.buildDir.exists()) {
            FileUtils.forceMkdir(project.buildDir)
        }


        File simplycdFile = new File(project.buildDir, "simplycd.json")
        objectMapper.writeValue(simplycdFile, jsonConfig)

        ThresholdsContainer thresholdsContainer = project.extensions.getByName('thresholds') as ThresholdsContainer
        File thresholdsFile = new File(project.buildDir, "thresholds.json")
        objectMapper.writeValue(thresholdsFile, thresholdsContainer)

    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg);
    }


}
