package uk.q3c.simplycd.lifecycle

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

class ThresholdsContainer extends AbstractNamedDomainObjectContainer<TestGroupThresholds> {

    String remoteRepoUserName = 'davidsowerby'

    ThresholdsContainer(Instantiator instantiator) {
        super(TestGroupThresholds.class, instantiator)
    }

    /**
     * copy constructor
     */
    ThresholdsContainer(ThresholdsContainer other) {
        super(TestGroupThresholds.class, other.instantiator)
        Iterator<TestGroupThresholds> iter = other.iterator()
        while (iter.hasNext()) {
            TestGroupThresholds otherEntry = iter.next()
            TestGroupThresholds newEntry = create(otherEntry.name)
            newEntry.copy(otherEntry)
        }
    }

    @Override
    protected TestGroupThresholds doCreate(String name) {
        return new TestGroupThresholds(name)
    }
}