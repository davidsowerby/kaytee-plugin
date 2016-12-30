package uk.q3c.simplycd.lifecycle

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Carries out most of the work for {@link CreateBuildInfoTask}, to enable testing

 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class CreateBuildInfoTaskDelegate {

    final Project project
    GitPlus gitPlus

    CreateBuildInfoTaskDelegate(Project project) {
        this.project = project
    }


    public void writeInfo() throws IOException {
        logLifecycle("creating build info file");
        final Properties properties = new Properties();
        properties.setProperty("version", getVersion());
        properties.setProperty("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        properties.setProperty("commit", tagAndGetCommitId());
        properties.setProperty("host", hostName());
        final Project project = getProject();
        final File buildResourcesDir = new File(project.getBuildDir(), "resources/main");
        final File buildInfoFile = new File(buildResourcesDir, "buildInfo.properties");

        // create the directory of not there - will only happen if task is run independently
        if (!buildResourcesDir.exists()) {
            FileUtils.forceMkdir(buildResourcesDir);
        }

        // write the file
        FileOutputStream fw
        try {
            fw = new FileOutputStream(buildInfoFile)
            properties.store(fw, "");
        } catch (Exception e) {
            logLifecycle("failed to write build file", e);
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
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logLifecycle("Unable to identify host", e);
            hostName = "unknown host";
        }
        return hostName;
    }

    /**
     * Operations tied together because they use Git.
     *
     * Tag the latest commit with the current version, taken from the project
     * Push tags back to origin
     * Return the commit id for the latest commit
     *
     * @return
     */
    private String tagAndGetCommitId() {

        final GitLocal gitLocal = gitPlus.getLocal();
        final GitLocalConfiguration localConfig = gitLocal.getLocalConfiguration();
        localConfig.projectName(getProject().getName());
        localConfig.projectDirParent(getProject().getProjectDir().getParentFile());
        logDebug("GitLocal config set with project directory = " + localConfig.projectDir());
        gitPlus.getRemote().getConfiguration().active(false);
        gitPlus.execute();
        gitLocal.tag(getVersion(), 'version ' + getVersion())
        gitLocal.push(true, false)
        final GitBranch currentBranch = gitLocal.currentBranch();
        return gitLocal.latestCommitSHA(currentBranch).getSha();
    }

    private void logLifecycle(String msg) {
        project.getLogger().lifecycle(msg);
    }

    private void logLifecycle(String msg, Throwable e) {
        project.getLogger().lifecycle(msg, e);
    }


    private void logDebug(String msg) {
        project.getLogger().debug(msg);
    }
}
