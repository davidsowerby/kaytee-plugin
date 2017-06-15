package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import uk.q3c.build.gitplus.GitSHA
/**
 * Created by David Sowerby on 20 Dec 2016
 */
class KayTeeVersion extends DelegateWithConfig {

    String baseVersion


    KayTeeVersion(Project project) {
        super(project)
    }


    @Override
    String toString() {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        boolean kayTeeConfigured = ext.get(KayTeePlugin.KAYTEE_CONFIG_FLAG)
        if (kayTeeConfigured) {
            prepare()
            baseVersion = config.baseVersion
            logDebug("Base version is $baseVersion")
            String fullVersion = baseVersion + "." + getCurrentCommit().short()
            logDebug("Full version is $fullVersion")
            return fullVersion
        } else {
            project.logger.debug("version not yet available, evaluation not complete")
            return 'version not available until evaluation complete'
        }
    }


    private GitSHA getCurrentCommit() {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        String commitId = ext.get(KayTeePlugin.KAYTEE_COMMIT_ID)
        return new GitSHA(commitId)
    }

/**
 * Removes the last element from the version, that is everything from, and including, the last '.'
 * For example 2.2.3.4.55 is returned as 2.2.3.4
 *
 * A tag name which contains no '.' is returned unchanged
 *
 * @param fullVersion
 * @return base version
 */
    static String baseVersionFromFullVersion(String fullVersion) {
        if (!fullVersion.contains(".")) {
            return fullVersion
        }
        int index = fullVersion.lastIndexOf('.')
        return fullVersion.substring(0, index)

    }

}
