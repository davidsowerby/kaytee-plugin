package uk.q3c.simplycd.lifecycle

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

class ThresholdsContainer extends AbstractNamedDomainObjectContainer<TestGroupThresholds> {

    String remoteRepoUserName = 'davidsowerby'

    ThresholdsContainer(Instantiator instantiator) {
        super(TestGroupThresholds.class, instantiator)
    }

    @Override
    protected TestGroupThresholds doCreate(String name) {
        return new TestGroupThresholds(name)
    }
}