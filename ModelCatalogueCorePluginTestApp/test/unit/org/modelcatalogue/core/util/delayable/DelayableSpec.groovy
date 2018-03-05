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
            delayable.add 'l0a'
            delayable.whilePaused {
                for (int i in 1..3) {
                    delayable.add('  l1b' + i)
                    delayable.whilePaused {
                        delayable.add('    l2b' + i)
                        if (i == 2) {
                            delayable.requestRun()
                        }
                        delayable.add('    l2c' + i)
                        delayable.whilePaused {
                            for (int j in 1..3) {
                                delayable.add('      l3c' + i + 'x' + j)
                                delayable.whilePaused {
                                    delayable.add('       l4d' + i + 'x' + j)
                                    if (i == 3) {
                                        delayable.requestRun()
                                    }
                                    delayable.add('       l4e' + i + 'x' + j)
                                }
                            }
                        }
                    }
                }
            }
        }
        expect:
        collector.join('\n') == '''
          l0a
            l1b1
            l1b2
              l2b2
              l2c2
            l1b3
              l2b3
              l2c3
                l3c3x1
                 l4d3x1
                 l4e3x1
                l3c3x2
                 l4d3x2
                 l4e3x2
                l3c3x3
                 l4d3x3
                 l4e3x3
        '''.stripIndent().trim()
    }

}
