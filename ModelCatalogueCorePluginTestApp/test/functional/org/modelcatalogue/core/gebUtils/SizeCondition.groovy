package org.modelcatalogue.core.gebUtils

class SizeCondition {

    final int number;
    final NavigatorCondition condition

    SizeCondition(int times, NavigatorCondition condition) {
        this.number = times
        this.condition = condition
    }

    boolean getTimes() {
        or(SizeConditionKeyword.EQUAL)
    }

    boolean or(SizeConditionKeyword keyword) {
        switch (keyword) {
            case SizeConditionKeyword.LESS: return condition.test(5) {
                it.size() <= number
            }
            case SizeConditionKeyword.MORE: return condition.test(5) {
                it.size() >= number
            }
            case SizeConditionKeyword.EQUAL: return condition.test(5) {
                it.size() == number
            }
            default: throw new IllegalArgumentException("Unknown keyword: $keyword")
        }
    }
}
