package org.modelcatalogue.core.security

import groovy.transform.CompileStatic
import org.modelcatalogue.core.persistence.RequestmapGormService
import org.springframework.http.HttpMethod

@CompileStatic
class MetadataSecurityService {
    RequestmapGormService requestmapGormService

    void secureUrlMappings() {
        //permit all for assets and initial pages
        for (String url in [
                '/',
                '/**/favicon.ico',
                '/fonts/**',
                '/stomp/**',
                '/assets/**',
                '/plugins/**/js/**',
                '/plugins/jquery-ui-*/**',
                '/js/vendor/**',
                '/**/*.less',
                '/**/js/**',
                '/**/css/**',
                '/**/images/**',
                '/**/img/**',
                '/login', '/login.*', '/login/*',
                '/logout', '/logout.*', '/logout/*',
                '/register/*', '/errors', '/errors/*',
                '/load',
                '/index.gsp'
        ]) {
            requestmapGormService.createRequestmapIfMissing(url, 'permitAll', null)
        }

        requestmapGormService.createRequestmapIfMissing('/asset/download/*',                      'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/oauth/*/**',                            'IS_AUTHENTICATED_ANONYMOUSLY')
        requestmapGormService.createRequestmapIfMissing('/user/current',                          'IS_AUTHENTICATED_ANONYMOUSLY',  HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/catalogue/upload',                      'ROLE_METADATA_CURATOR',         HttpMethod.POST)
        requestmapGormService.createRequestmapIfMissing('/catalogue/*/**',                        'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',   HttpMethod.GET)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/*/comments',  'isAuthenticated()',   HttpMethod.POST) // post a comment
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/user/*/favourite', 'isAuthenticated()',HttpMethod.POST) // favourite item
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/user/apikey',    'isAuthenticated()',HttpMethod.POST) // get or create new api key
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/user/*/favourite', 'isAuthenticated()',HttpMethod.DELETE) // unfavourite item
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.POST)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.PUT)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**',          'isAuthenticated()',         HttpMethod.DELETE)
        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/asset/*/validateXML',  'isAuthenticated()',   HttpMethod.POST) // validate xml

        requestmapGormService.createRequestmapIfMissing('/sso/*/**',                              'isAuthenticated()',   HttpMethod.GET)

        requestmapGormService.createRequestmapIfMissing('/role/**',                               'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/userAdmin/**',                          'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/requestMap/**',                         'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/registrationCode/**',                   'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/securityInfo/**',                       'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/console/**',                            'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/plugins/console*/**',                   'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/dbconsole/**',                          'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/monitoring/**',                         'ROLE_SUPERVISOR')
        requestmapGormService.createRequestmapIfMissing('/plugins/console-1.5.0/**',              'ROLE_SUPERVISOR')

//        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataClass/**', 'IS_AUTHENTICATED_ANONYMOUSLY')
//        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataElement/**', 'ROLE_METADATA_CURATOR')
//        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/dataType/**', 'ROLE_USER')
//        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/*/**', 'ROLE_METADATA_CURATOR')
//        requestmapGormService.createRequestmapIfMissing('/api/modelCatalogue/core/relationshipTypes/**', 'ROLE_ADMIN')
    }
}
