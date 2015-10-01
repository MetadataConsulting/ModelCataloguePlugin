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

}
