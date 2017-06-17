package uk.q3c.kaytee.plugin

import org.apache.commons.lang.StringUtils

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

    def validate(TaskKey group, List<String> errors) {
        String groupLog = "Test group '${group.name()}'"
        if (StringUtils.isEmpty(commitId)) {
            errors.add("$groupLog: commitId cannot be null or empty for a delegated task")
        }
        if (StringUtils.isEmpty(baseUrl)) {
            errors.add("$groupLog: baseUrl cannot be null or empty for a delegated task")
        }
        if (StringUtils.isEmpty(repoUserName)) {
            errors.add("$groupLog: repoUserName cannot be null or empty for a delegated task")
        }
        if (StringUtils.isEmpty(taskToRun)) {
            errors.add("$groupLog: taskToRun cannot be null or empty for a delegated task")
        }
        if (StringUtils.isEmpty(branch)) {
            errors.add("$groupLog: branch cannot be null or empty for a delegated task")
        }
        if (StringUtils.isEmpty(repoName)) {
            errors.add("$groupLog: repoName cannot be null or empty for a delegated task")
        }
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
