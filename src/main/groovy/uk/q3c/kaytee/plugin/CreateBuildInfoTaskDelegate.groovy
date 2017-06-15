package uk.q3c.kaytee.plugin

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
/**
 * Carries out most of the work for {@link uk.q3c.kaytee.plugin.CreateBuildInfoTask}, to enable testing

 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class CreateBuildInfoTaskDelegate extends DelegateWithConfig {

    VersionCheckTaskDelegate versionCheck
    static String PATH_TO_BUILD_INFO_PROPS = 'resources/main/buildInfo.properties'
    static String PROPERTY_NAME_COMMIT_ID = "commitId"


    CreateBuildInfoTaskDelegate(Project project, VersionCheckTaskDelegate versionCheck) {
        super(project)
        this.versionCheck = versionCheck
    }

/**
 * Writes build information into file 'buildInfo.properties' in the build directory (therefore not making changes to source).
 *
 *
 * @throws IOException
 */
    void writeInfo() throws IOException {
        logLifecycle("creating build info file")
        prepare()
        final Properties properties = new Properties()
        final String baseVersion = config.baseVersion
        properties.setProperty("baseVersion", baseVersion)
        properties.setProperty("date", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        String commitId = getCommitId()
        properties.setProperty(PROPERTY_NAME_COMMIT_ID, commitId)
        properties.setProperty("host", hostName())
        final Project project = getProject()
        final File buildResourcesDir = new File(project.getBuildDir(), "resources/main")
        final File buildInfoFile = new File(buildResourcesDir, "buildInfo.properties")

        // create the directory if not there - will only happen if task is run independently
        if (!buildResourcesDir.exists()) {
            FileUtils.forceMkdir(buildResourcesDir)
        }

        writeFile(properties, buildInfoFile)
        properties.setProperty("version", getVersion())
        writeFile(properties, buildInfoFile)
//        tag(commitId)
    }

    void writeFile(Properties properties, File buildInfoFile) {
        // write the file
        FileOutputStream fw
        try {
            fw = new FileOutputStream(buildInfoFile)
            properties.store(fw, "")
            logLifecycle("buildInfo file created or updated at " + buildInfoFile.getAbsolutePath())
        } catch (Exception e) {
            logLifecycle("failed to write build file", e)
        } finally {
            if (fw != null) {
                fw.close()
            }
        }
    }

    private String getVersion() {
        return project.getVersion().toString()
    }


    private String hostName() {
        String hostName
        try {
            hostName = InetAddress.getLocalHost().getHostName()
        } catch (UnknownHostException e) {
            logLifecycle("Unable to identify host", e)
            hostName = "unknown host"
        }
        return hostName
    }


    private String getCommitId() {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties()
        String commitId = ext.get(KayTeePlugin.KAYTEE_COMMIT_ID)
        return commitId
    }

    /**
     * It is entirely possible that a tag for the commit being built has already been created - a build re-run for example.
     * Existence of tag is checked first, and if already there, no action taken
     *
     * @return the tag value, either created or confirmed
     */
//    private String tag(String commitId) {
//        logLifecycle("tagging $commitId")
//        final GitLocal gitLocal = gitPlus.getLocal()
//        String version = getVersion()
//        if (versionCheck.check()) {
//            return version
//        }
//        gitLocal.tag(version, 'version ' + version)
//        gitLocal.push(true, false)
//        logLifecycle("Git tagged as version " + version)
//        return version
//    }


}
