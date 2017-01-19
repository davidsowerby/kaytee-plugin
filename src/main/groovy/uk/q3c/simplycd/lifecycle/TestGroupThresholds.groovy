package uk.q3c.simplycd.lifecycle

import org.gradle.api.Named

class TestGroupThresholds implements Named {
    double instruction = 81
    double branch = 70
    double line = 90
    double complexity = 90
    double method = 90
    double clazz = 90
    String name

    TestGroupThresholds(String name) {
        this.name = name
    }

    @Override
    String getName() {
        return name
    }
}