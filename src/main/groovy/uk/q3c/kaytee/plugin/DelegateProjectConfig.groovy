package uk.q3c.kaytee.plugin

/**
 * Created by David Sowerby on 05 Jun 2017
 */
class DelegateProjectConfig {
    String baseUrl = "https://github.com"
    String repoUserName = ""
    String repoName = ""
    String taskToRun = "test"
    String branch = "develop"
    String commitId = ""

    DelegateProjectConfig() {
    }

    DelegateProjectConfig(DelegateProjectConfig other) {
        baseUrl = other.baseUrl
        repoUserName = other.repoUserName
        repoName = other.repoName
        taskToRun = other.taskToRun
        branch = other.branch
        commitId = other.commitId
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DelegateProjectConfig that = (DelegateProjectConfig) o

        if (baseUrl != that.baseUrl) return false
        if (branch != that.branch) return false
        if (commitId != that.commitId) return false
        if (repoName != that.repoName) return false
        if (repoUserName != that.repoUserName) return false
        if (taskToRun != that.taskToRun) return false

        return true
    }

    int hashCode() {
        int result
        result = baseUrl.hashCode()
        result = 31 * result + repoUserName.hashCode()
        result = 31 * result + repoName.hashCode()
        result = 31 * result + taskToRun.hashCode()
        result = 31 * result + branch.hashCode()
        result = 31 * result + commitId.hashCode()
        return result
    }
}
