package uk.q3c.kaytee.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jfrog.bintray.gradle.BintrayExtension

/**
 *
 * The BintrayExtension has non-data properties which mess up Jackson.  This class just extracts the data
 *
 * Created by David Sowerby on 24 May 2017
 */
class BintrayConfigWrapper {

    String apiUrl

    String user

    String key

    String[] configurations

    String[] publications

    boolean publish

    boolean override

    boolean dryRun

    BintrayExtension.PackageConfig pkg

    BintrayConfigWrapper(BintrayExtension extension) {
        apiUrl = extension.apiUrl
        user = extension.user
        if (extension.key != null) {
            key = "key of ${extension.key.length()} length"
        }

        configurations = extension.configurations
        publications = extension.publications
        publish = extension.publish
        override = extension.override
        dryRun = extension.dryRun
        pkg = extension.pkg
    }


    @Override
    String toString() {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        StringWriter sw = new StringWriter()
        objectMapper.writeValue(sw, this)
        return sw.toString()
    }
}
