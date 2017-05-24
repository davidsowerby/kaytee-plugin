package uk.q3c.simplycd.lifecycle

import com.jfrog.bintray.gradle.BintrayExtension

/**
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
        key = extension.key
        configurations = extension.configurations
        publications = extension.publications
        publish = extension.publish
        override = extension.override
        dryRun = extension.dryRun
        pkg = extension.pkg
    }
}
