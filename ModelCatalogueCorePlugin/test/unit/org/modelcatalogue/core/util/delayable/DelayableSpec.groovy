package org.modelcatalogue.core.util.delayable

import spock.lang.Specification


class DelayableSpec extends Specification {

    def "Can delay execution"() {
        List<String> collector = []
        Delayable<List<String>> delayable = new Delayable<List<String>>(collector)

        when:
        collector.add 'a'

        then:
        collector.join('') == 'a'

        when:
        delayable.add 'b'

        then:
        collector.join('') == 'ab'

        when:
        delayable.pauseAndRecord()
        delayable.add 'c'
        delayable.add 'd'

        then:
        collector.join('') == 'ab'

        when:
        delayable.run()

        then:
        collector.join('') == 'abcd'

        when:
        delayable.pauseAndRecord()
        delayable.add 'e'
        delayable.resetAndUnpause()
        delayable.add 'f'

        then:
        collector.join('') == 'abcdf'

    }

    def "complex nesting"() {
        List<String> collector = []
        Delayable<List<String>> delayable = new Delayable<List<String>>(collector)

        delayable.whilePaused {
            whilePaused {
                add 'a'
            }

            reset()

            add 'b'

            whilePaused(true) {
                add 'c'
                reset()
                add 'd'
            }

            whilePaused(true) {
                add 'e'
                requestRun()
                add 'f'
            }

            add 'g'

            whilePaused {
                add 'h'
                reset()
                add 'i'
            }
        }

        expect:
        collector.join('') == 'befgi'

    }
}
