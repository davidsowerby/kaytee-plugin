package uk.q3c.kaytee.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import java.text.DecimalFormat

/**
 * Created by David Sowerby on 27 Dec 2016
 */
class QualityGateTask extends DefaultTask {
    private String testGroup = "unspecified"
    private final Map<String, Double> thresholds = new HashMap<>(10)

    Map<String, Double> getThresholds() {
        return thresholds
    }

    String getTestGroup() {

        return testGroup
    }

    void setTestGroup(String testGroup) {
        this.testGroup = testGroup
    }

    /**
     * Evaluates the code coverage results against the thresholds
     */
    @TaskAction
    void evaluate() {
        getLogger().debug("evaluating '" + testGroup + "' results against required thresholds")

        final KayTeeExtension config = getProject().getExtensions().findByName("kaytee") as KayTeeExtension
        final TestGroupThresholds thresholds = config.testConfig(testGroup).thresholds
        setThresholdsFromConfiguration(thresholds)


        final File baseReportsDir = new File(getProject().getBuildDir(), "reports/jacoco")
        getLogger().debug(baseReportsDir.getAbsolutePath())
        final String reportFolderName = testGroup + "Report"
        final String reportFileName = reportFolderName + ".xml"
        final File reportDir = new File(baseReportsDir, reportFolderName)
        final File reportFile = new File(reportDir, reportFileName)
        getLogger().debug("Reading coverage results from: " + reportFile.getAbsolutePath())

        final Node results = parseFile(reportFile)

        final Map<String, Node> resultsMap = extractResults(results)

        // check for failures
        final Map<String, Double> failures = identifyFailures(resultsMap)

        presentFailures(failures)

    }

    private void setThresholdsFromConfiguration(TestGroupThresholds testConfig) {
        thresholds.put("instruction", testConfig.getInstruction())
        thresholds.put("class", testConfig.getClazz())
        thresholds.put("branch", testConfig.getBranch())
        thresholds.put("complexity", testConfig.getComplexity())
        thresholds.put("line", testConfig.getLine())
        thresholds.put("method", testConfig.getMethod())

    }

    private void presentFailures(Map<String, Double> failures) {
        if (failures.isEmpty()) {
            getLogger().quiet("'" + testGroup + "' passed quality gate (code coverage met required thresholds)")
        } else {
            getLogger().quiet("------------------ Code Coverage Failed -----------------------")
            final DecimalFormat df = new DecimalFormat("#.0")
            for (final Map.Entry<String, Double> fail : failures.entrySet()) {
                final String measure = fail.getKey()
                final Double actual = fail.getValue()
                final String actualStr = df.format(actual)
                final double required = thresholds.get(measure.toLowerCase())
                getLogger().quiet(measure + " result is " + actualStr + "%, but " + required + "% is required")
            }
            getLogger().quiet("---------------------------------------------------------------")
            throw new GradleException("Code coverage failed")
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private Node parseFile(File reportFile) {
        try {
            final XmlParser parser = new XmlParser()
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            return parser.parse(reportFile)

        } catch (Exception e) {
            throw new GradleException(e.getClass()
                    .getSimpleName() + " occurred in QualityGateTask for " + testGroup, e)
        }
    }

    private Map<String, Node> extractResults(Node results) {
        final Map<String, Node> resultsMap = new HashMap<>(10)
        for (final Object child : results.children()) {
            final Node childNode = (Node) child
            if ("counter".equals(childNode.name())) {
                resultsMap.put((String) childNode.get("@type"), childNode)
            }
        }
        return resultsMap
    }

    private Map<String, Double> identifyFailures(Map<String, Node> resultsMap) {
        getLogger().debug('Examining thresholds for ' + testGroup)
        for (String s : thresholds.keySet()) {
            getLogger().debug(s + ' = ' + thresholds.get(s))
        }
        final Map<String, Double> failures = new HashMap<>(10)
        for (final Map.Entry<String, Node> entry : resultsMap.entrySet()) {
            final int covered = Integer.parseInt(entry.getValue()
                    .get("@covered")
                    .toString())
            final int missed = Integer.parseInt(entry.getValue()
                    .get("@missed")
                    .toString())
            getLogger().debug("entry for " + entry.getKey() + ", covered = " + covered + ", missed = " + missed)
            final int total = missed + covered
            final double percentageAchieved = ((double) covered / (double) total) * 100.0
            final double threshold = thresholds.get(entry.getKey()
                    .toLowerCase())
            if (percentageAchieved < threshold) {
                failures.put(entry.getKey(), percentageAchieved)
            }
        }
        return failures
    }

}
