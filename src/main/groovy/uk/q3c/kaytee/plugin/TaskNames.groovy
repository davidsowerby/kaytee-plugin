package uk.q3c.kaytee.plugin

/**
 * Created by David Sowerby on 27 Jan 2017
 */
class TaskNames {

    public final static String GENERATE_BUILD_INFO_TASK_NAME = 'generateBuildInfo'
    public final static String GENERATE_CHANGE_LOG_TASK_NAME = 'generateChangeLog'
    public final static String EXTRACT_CONFIG_TASK_NAME = 'kayteeConfigToJson'

    public final static String UNIT_TEST = 'test'
    public final static String UNIT_TEST_QUALITY_GATE = 'testQualityGate'

    public final static String INTEGRATION_TEST = 'integrationTest'
    public final static String INTEGRATION_QUALITY_GATE = 'integrationTestQualityGate'

    public final static String FUNCTIONAL_TEST = 'functionalTest'
    public final static String FUNCTIONAL_QUALITY_GATE = 'functionalTestQualityGate'

    public final static String ACCEPTANCE_TEST = 'acceptanceTest'
    public final static String ACCEPTANCE_QUALITY_GATE = 'acceptanceTestQualityGate'


    public final static String PRODUCTION_TEST = 'productionTest'
    public final static String PRODUCTION_QUALITY_GATE = 'productionTestQualityGate'

    public final static String PUBLISH_TO_LOCAL = 'publishToMavenLocal'
    public final static String MERGE_TO_MASTER = 'mergeToMaster'
    public final static String SET_VERSION = 'versionCheck'
    public final static String BINTRAY_UPLOAD = 'bintrayUpload'
    public final static String BINTRAY_CONFIG_TO_JSON = 'bintrayConfigToJson'
    public final static String CUSTOM = 'custom'
    public final static String TAG = 'tag'

}
