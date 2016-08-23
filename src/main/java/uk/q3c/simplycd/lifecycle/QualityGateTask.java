package uk.q3c.simplycd.lifecycle;

import groovy.util.Node;
import groovy.util.XmlParser;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by David Sowerby on 08 Aug 2016
 */
@SuppressWarnings({"MagicNumber", "PublicMethodNotExposedInInterface", "ReturnOfCollectionOrArrayField", "AssignmentToCollectionOrArrayFieldFromParameter",
        "DuplicateStringLiteralInspection"})
public class QualityGateTask extends DefaultTask {
    private static final Map<String, Integer> defaultThresholds;

    static {
        defaultThresholds = new HashMap<>(6);
        defaultThresholds.put("instruction", 81);
        defaultThresholds.put("branch", 70);
        defaultThresholds.put("line", 90);
        defaultThresholds.put("complexity", 90);
        defaultThresholds.put("method", 90);
        defaultThresholds.put("class", 90);
    }

    private String testGroup = "unspecified";
    private Map<String, Integer> thresholds = defaultThresholds;

    public Map<String, Integer> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<String, Integer> thresholds) {
        this.thresholds = thresholds;
    }

    public String getTestGroup() {

        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    @SuppressWarnings({"CallToStringEquals", "StringConcatenationMissingWhitespace", "HardcodedFileSeparator", "PublicMethodWithoutLogging",
            "MagicCharacter"})
    @TaskAction
    /**
     * Evaluates the code coverage results against the {@link #thresholds}
     */
    public void evaluate() {
        getLogger().lifecycle("evaluating test results against thresholds");
        final File baseReportsDir = new File(getProject().getBuildDir(), "reports/jacoco");
        getLogger().lifecycle(baseReportsDir.getAbsolutePath());
        final String reportFolderName = testGroup + "Report";
        final String reportFileName = reportFolderName + ".xml";
        final File reportDir = new File(baseReportsDir, reportFolderName);
        final File reportFile = new File(reportDir, reportFileName);
        getLogger().lifecycle("Reading coverage results from: " + reportFile.getAbsolutePath());

        final Node results = parseFile(reportFile);

        final Map<String, Node> resultsMap = extractResults(results);

        // check for failures
        final Map<String, Double> failures = identifyFailures(resultsMap);

        presentFailures(failures);

    }

    private void presentFailures(Map<String, Double> failures) {
        if (failures.isEmpty()) {
            getLogger().quiet("Passed Code Coverage Checks");
        } else {
            getLogger().quiet("------------------ Code Coverage Failed -----------------------");
            final DecimalFormat df = new DecimalFormat("#.0");
            for (final Entry<String, Double> fail : failures.entrySet()) {
                final String measure = fail.getKey();
                final Double actual = fail.getValue();
                final String actualStr = df.format(actual);
                final int required = thresholds.get(measure.toLowerCase());
                getLogger().quiet(measure + " coverage is " + actualStr + "%, but " + required + "% is required");
            }
            getLogger().quiet("---------------------------------------------------------------");
            throw new GradleException("Code coverage failed");
        }
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private Node parseFile(File reportFile) {
        try {
            final XmlParser parser = new XmlParser();
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            return parser.parse(reportFile);

        } catch (Exception e) {
            throw new GradleException(e.getClass()
                                       .getSimpleName() + " occurred in QualityGateTask for " + testGroup, e);
        }
    }

    private Map<String, Node> extractResults(Node results) {
        final Map<String, Node> resultsMap = new HashMap<>(10);
        for (final Object child : results.children()) {
            final Node childNode = (Node) child;
            if ("counter".equals(childNode.name())) {
                resultsMap.put((String) childNode.get("@type"), childNode);
            }
        }
        return resultsMap;
    }

    private Map<String, Double> identifyFailures(Map<String, Node> resultsMap) {
        final Map<String, Double> failures = new HashMap<>(10);
        for (final Entry<String, Node> entry : resultsMap.entrySet()) {
            final int covered = Integer.parseInt(entry.getValue()
                                                      .get("@covered")
                                                      .toString());
            final int missed = Integer.parseInt(entry.getValue()
                                                     .get("@missed")
                                                     .toString());
            final int total = missed + covered;
            final double percentageAchieved = ((double) covered / (double) total) * 100.0;
            final double threshold = thresholds.get(entry.getKey()
                                                         .toLowerCase());
            if (percentageAchieved < threshold) {
                failures.put(entry.getKey(), percentageAchieved);
            }
        }
        return failures;
    }

}
