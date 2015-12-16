package org.modelcatalogue.core.geb

class SizeCondition {

    final int times;
    final NavigatorCondition condition

    SizeCondition(int times, NavigatorCondition condition) {
        this.times = times
        this.condition = condition
    }

    boolean getTimes() {
        condition.test(5) { it.size() == times }
    }
}