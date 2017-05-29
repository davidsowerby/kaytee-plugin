package uk.q3c.kaytee.plugin

class TestGroupThresholds {
    double instruction = 81
    double branch = 70
    double line = 90
    double complexity = 90
    double method = 90
    double clazz = 90

    TestGroupThresholds() {
    }

    TestGroupThresholds(TestGroupThresholds other) {
        this.instruction = other.instruction
        this.branch = other.branch
        this.line = other.line
        this.complexity = other.complexity
        this.method = other.method
        this.clazz = other.clazz
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        TestGroupThresholds that = (TestGroupThresholds) o

        if (Double.compare(that.branch, branch) != 0) return false
        if (Double.compare(that.clazz, clazz) != 0) return false
        if (Double.compare(that.complexity, complexity) != 0) return false
        if (Double.compare(that.instruction, instruction) != 0) return false
        if (Double.compare(that.line, line) != 0) return false
        if (Double.compare(that.method, method) != 0) return false

        return true
    }

    int hashCode() {
        int result
        long temp
        temp = instruction != +0.0d ? Double.doubleToLongBits(instruction) : 0L
        result = (int) (temp ^ (temp >>> 32))
        temp = branch != +0.0d ? Double.doubleToLongBits(branch) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        temp = line != +0.0d ? Double.doubleToLongBits(line) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        temp = complexity != +0.0d ? Double.doubleToLongBits(complexity) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        temp = method != +0.0d ? Double.doubleToLongBits(method) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        temp = clazz != +0.0d ? Double.doubleToLongBits(clazz) : 0L
        result = 31 * result + (int) (temp ^ (temp >>> 32))
        return result
    }
}