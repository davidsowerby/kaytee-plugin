package uk.q3c.simplycd.lifecycle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

/**
 * Created by David Sowerby on 24 Dec 2016
 */
class AfterEvaluateAction implements Action<Project> {

    final Project project

    def AfterEvaluateAction(Project project) {
        this.project = project
    }

    @Override
    void execute(Project project) {
        project.getLogger().lifecycle('after evaluate')

        project.tasks.create(name: 'sourcesJar', type: Jar, dependsOn: project.classes) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }

        project.tasks.create(name: 'javadocJar', type: Jar, dependsOn: project.javadoc) {
            classifier = 'javadoc'
            from project.javadoc.destinationDir
        }

        project.publishing {
            publications {
                mavenStuff(MavenPublication) {
                    from project.components.java

                    artifact project.sourcesJar {
                        classifier "sources"
                    }

                    artifact project.javadocJar {
                        classifier "javadoc"
                    }
                }
            }
        }

    }

    private void log(String s) {
        project.getLogger().lifecycle(s)
    }
}
