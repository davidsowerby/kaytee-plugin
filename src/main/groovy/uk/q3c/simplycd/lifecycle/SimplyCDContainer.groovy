package uk.q3c.simplycd.lifecycle

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

class SimplyCDContainer extends AbstractNamedDomainObjectContainer<TestConfiguration> {

    String remoteRepoUserName = 'davidsowerby'

    SimplyCDContainer(Instantiator instantiator) {
        super(TestConfiguration.class, instantiator)
    }

    @Override
    protected TestConfiguration doCreate(String name) {
        return new TestConfiguration(name)
    }
}