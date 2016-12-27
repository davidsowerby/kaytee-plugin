package uk.q3c.simplycd.lifecycle

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import uk.q3c.build.gitplus.GitPlusFactory
import uk.q3c.build.gitplus.gitplus.GitPlus
import uk.q3c.build.gitplus.local.GitBranch
import uk.q3c.build.gitplus.local.GitLocal
import uk.q3c.build.gitplus.local.GitLocalConfiguration

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Creates a properties file containing build information and writes it to the build directory, in folder 'main/resources'
 * This ensures it is included in the jar
 * <p>
 * Created by David Sowerby on 05 Dec 2016
 */
class CreateBuildInfoTask extends DefaultTask {

    public static final String BASE_VERSION = "baseVersion";


    @TaskAction
    public void writeInfo() throws IOException {
        getLogger().lifecycle("creating build info file");
        final Properties properties = new Properties();
        properties.setProperty("version", getProject().getVersion().toString());
        properties.setProperty("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        properties.setProperty("commit", commitId());
        properties.setProperty("host", hostName());
        final Project project = getProject();
        final File buildResourcesDir = new File(project.getBuildDir(), "resources/main");
        final File buildInfoFile = new File(buildResourcesDir, "buildInfo.properties");
        if (!buildResourcesDir.exists()) {
            FileUtils.forceMkdir(buildResourcesDir);
        }
        try {
            FileOutputStream fw = new FileOutputStream(buildInfoFile)
            properties.store(fw, "");
        } catch (Exception e) {
            getLogger().lifecycle("failed to write build file", e);
        } finally {
            fw.close()
        }
    }

    private String hostName() {
        String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            getLogger().lifecycle("Unable to identify host", e);
            hostName = "unknown host";
        }
        return hostName;
    }

    private String commitId() {
        String commitId;
        try {
            GitPlus gitPlus = GitPlusFactory.getInstance()
            final GitLocal gitLocal = gitPlus.getLocal();
            final GitLocalConfiguration localConfig = gitLocal.getLocalConfiguration();
            localConfig.projectName(getProject().getName());
            localConfig.projectDirParent(getProject().getProjectDir().getParentFile());
            logDebug("GitLocal config set with project directory = " + localConfig.projectDir());
            gitPlus.getRemote().getConfiguration().active(false);
            gitPlus.execute();
            final GitBranch currentBranch = gitLocal.currentBranch();
            commitId = gitLocal.latestCommitSHA(currentBranch).getSha();
        } catch (Exception e) {
            final String msg = "Unable to construct GitPlus, and therefore unable to read commit id";
            getLogger().lifecycle(msg, e);
            commitId = msg;
        } finally {
            gitPlus.close()
        }

        return commitId;
    }

    private void log(String msg) {
        getLogger().lifecycle(msg);
    }

    private void logDebug(String msg) {
        getLogger().debug(msg);
    }
}