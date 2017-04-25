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

    TestGroupThresholds(TestGroupThresholds other) {
        copy(other)
    }

    TestGroupThresholds(String name) {
        this.name = name
    }

    void copy(TestGroupThresholds other) {
        this.instruction = other.instruction
        this.branch = other.branch
        this.line = other.line
        this.complexity = other.complexity
        this.method = other.method
        this.clazz = other.clazz
        this.name = other.name
    }

    @Override
    String getName() {
        return name
    }
}