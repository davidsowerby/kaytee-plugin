package uk.q3c.simplycd.lifecycle;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by David Sowerby on 15 Aug 2016
 */
public class SimplyCDPluginExtension {

    private List<String> testSets = ImmutableList.of("unit", "integration", "functional", "acceptance", "smoke");

    public List<String> getTestSets() {
        return testSets;
    }

    public void setTestSets(List<String> testSets) {
        this.testSets = testSets;
    }

}
