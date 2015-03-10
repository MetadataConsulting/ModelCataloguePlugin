
classification name: 'Domains', {
    valueDomain name: 'Same'
}

classification name: 'One', {

    globalSearchFor valueDomain

    model name: 'Model', {
        dataElement name: 'ABC', {
            valueDomain name: 'Same', classification: 'Domains'
        }
        dataElement name: 'DEF'
        dataElement name: 'GHJ', {
            valueDomain name: 'Same', classification: 'Domains'
        }
        dataElement name: 'IJK'

        model name: 'MNO'
        model name: 'PQR'

    }
}

classification name: 'Two', {

    globalSearchFor valueDomain

    model name: 'Model', {
        dataElement name: 'ABC', {
            valueDomain name: 'Same', classification: 'Domains'
        }
        dataElement name: 'DEF'
        dataElement name: 'GHJ', {
            valueDomain name: 'Same', classification: 'Domains'
        }
        dataElement name: 'IJK'

        model name: 'MNO'
        model name: 'PQR'

    }
}

