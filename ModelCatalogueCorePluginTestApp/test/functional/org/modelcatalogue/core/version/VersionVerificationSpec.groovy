package org.modelcatalogue.core.version

import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

@Issue('https://metadata.atlassian.net/browse/MET-1925')
@Title('Version verification')
@Narrative('''
- Login to model catalogue | As User
- On the top right hand menu click on settings button to open drop-down menu
- Scroll down and select the Code Version option | you are redirected to a new page/window with the version number
- Check that the model catalogue carries the correct version
''')
class VersionVerificationSpec extends Specification {
}
