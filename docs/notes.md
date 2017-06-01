

User must specify:

- remote repo user name
- remote repo name is taken to be project name

project.version should only be defined if overriding default behaviour of {baseVersion}.{short Git hash}

- Recommend a base version (or it comes out as 0.0.0.0)

Changes to Bintray defaults:

        project.bintray {
            publications = ['mavenStuff'] //When uploading Maven-based publication files
            publish = true
            dryRun = false
            pkg{
                version{
                    publicDownloadNumbers = true
                }
            }
        }
        
        
QualityGate task is added even if quality gate is disabled in kaytee configuration - this allows you to run the quality gate
 directly from Gradle.
 
 TestSets are only added where test set is enabled