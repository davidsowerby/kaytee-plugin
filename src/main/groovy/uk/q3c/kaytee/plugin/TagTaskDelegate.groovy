package uk.q3c.kaytee.plugin

import org.gradle.api.Project
import uk.q3c.build.gitplus.gitplus.GitPlus

/**
 * Created by David Sowerby on 16 Jun 2017
 */
class TagTaskDelegate extends DelegateWithGitPlus {

    TagTaskDelegate(Project project) {
        super(project)
    }

    TagTaskDelegate(Project project, GitPlus gitPlus) {
        super(project, gitPlus)
    }

    String tagHead() {
        prepare()
        boolean rerun = ext.get(KayTeePlugin.KAYTEE_RERUN) as boolean
        logDebug("Property ${KayTeePlugin.KAYTEE_RERUN} is present and has value of: $rerun")
        if (!rerun) {
            KayTeeVersion version = new KayTeeVersion(project)
            String v = version.toString()
            String desc = "version $v"
            logDebug("applying tag with name $v to HEAD")
            gitPlus.local.tag(v, desc)
        } else {
            logDebug("This is a rerun, no tag applied")
        }
    }
}
