package uk.q3c.simplycd.lifecycle

import org.gradle.api.Project
import uk.q3c.build.gitplus.remote.DefaultGitRemoteUrlMapper

/**
 * Created by David Sowerby on 20 May 2017
 */
class PrepareBintrayDelegateTask {

    private Project project

    PrepareBintrayDelegateTask(Project project) {
        this.project = project
    }

    void prepare() {
        SimplyCDProjectExtension simplyCdConfig = project.extensions.getByName("simplycd") as SimplyCDProjectExtension
        DefaultGitRemoteUrlMapper mapper = new DefaultGitRemoteUrlMapper()
        mapper.parent = simplyCdConfig.gitRemoteConfiguration

        project.bintray {
            publications = ['mavenStuff']
            pkg {
                websiteUrl = mapper.repoBaselUrl()
                issueTrackerUrl = mapper.issuesUrl()
                vcsUrl = mapper.cloneUrl()
            }
            key = project.bintrayKey
        }
    }
}