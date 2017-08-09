import groovy.json.JsonSlurper

// config
grails.logging.jul.usebridge = false

grails.serverURL = "${System.getenv('METADATA_SCHEME') ?: 'http'}://${System.getenv('VIRTUAL_HOST') ?: System.getenv('METADATA_HOST') ?: System.getenv('DOCKER_MACHINE_IP') ?: new URL("http://checkip.amazonaws.com").text.trim()}"

grails.plugin.springsecurity.auth.loginFormUrl = "${grails.serverURL}/login/auth"
grails.plugin.springsecurity.successHandler.ajaxSuccessUrl = "${grails.serverURL}/login/ajaxSuccess"
grails.plugin.springsecurity.failureHandler.ajaxAuthFailUrl = "${grails.serverURL}/login/authfail"
grails.plugin.springsecurity.logout.afterLogoutUrl = grails.serverURL
grails.plugin.springsecurity.successHandler.defaultTargetUrl = grails.serverURL

// datasource
dataSource {

    def metadataDbPassword = System.getenv('METADATA_PASSWORD') ?: System.getenv('MC_MYSQL_ENV_MYSQL_PASSWORD') ?: System.getenv('RDS_PASSWORD') ?: 'metadata'
    def metadataDbUsername = System.getenv('METADATA_USERNAME') ?: System.getenv('MC_MYSQL_ENV_MYSQL_USER') ?: System.getenv('RDS_USERNAME') ?: 'metadata'
    def metadataJdbcString = System.getenv('METADATA_JDBC_URL')

    if (!metadataJdbcString && System.getenv("MC_MYSQL_NAME")) {
        metadataJdbcString = "mc-test-db2.cv0pol2cf5cc.eu-west-1.rds.amazonaws.com
    }

    if (!metadataJdbcString && System.getenv("RDS_HOSTNAME")) {
        metadataJdbcString = "jdbc:mysql://${System.getenv('RDS_HOSTNAME')}:${System.getenv('RDS_PORT')}/${System.getenv('RDS_DB_NAME')}?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
    }

    if (metadataJdbcString) {
        driverClassName = "com.mysql.jdbc.Driver"
        dialect='org.hibernate.dialect.MySQL5InnoDBDialect'
        url = metadataJdbcString
        username = metadataDbUsername
        password = metadataDbPassword
        dbCreate = "update"
        properties {
            maxActive = -1
            minEvictableIdleTimeMillis=1800000
            timeBetweenEvictionRunsMillis=1800000
            numTestsPerEvictionRun=3
            testOnBorrow=true
            testWhileIdle=true
            testOnReturn=false
            validationQuery="SELECT 1"
            jdbcInterceptors="ConnectionState"
        }
    } else {
        def dbdir = "${System.properties['catalina.base']}/db"

        dbCreate = "update"
        url = "jdbc:h2:file:${dbdir};MVCC=TRUE;LOCK_TIMEOUT=10000"
        pooled = true
        username = "sa"
        password = "db!admin"

        properties {
            maxActive = -1
            minEvictableIdleTimeMillis=1800000
            timeBetweenEvictionRunsMillis=1800000
            numTestsPerEvictionRun=3
            testOnBorrow=true
            testWhileIdle=true
            testOnReturn=true
            validationQuery="SELECT 1"
        }
    }


}

if (System.getenv("MC_ES_NAME")) {
    // mc-es is bound
    mc.search.elasticsearch.host='mc-es'
} else if (System.getenv('MC_ES_HOST')) {
    mc.search.elasticsearch.host=System.getenv('MC_ES_HOST')
    mc.search.elasticsearch.port=System.getenv('MC_ES_PORT')
} else {
    mc.search.elasticsearch.local="${System.properties['catalina.base']}/es"
}

grails.plugin.console.enabled=true

mc.legacy.dataModels=true

if (System.getenv("MC_DB_MIGRATE")) {
    // you can e.g. create separate container just for database migration
    grails.plugin.databasemigration.updateOnStart=true
    grails.plugin.databasemigration.updateOnStartFileNames=["changelog.groovy"]
}

if (System.getenv("MC_USE_LOCAL_STORAGE")) {
    // you can e.g. create separate container just for database migration
    mc.storage.directory="${System.properties['catalina.base']}/storage"
} else {
    mc.storage.directory = null
}

// this must be set to be able to send any mails
if (System.getenv("MC_MAIL_FROM")) {
    grails.mail.default.from = System.getenv("MC_MAIL_FROM")
    grails.plugin.springsecurity.ui.register.emailFrom = System.getenv("MC_MAIL_FROM")
    grails.plugin.springsecurity.ui.forgotPassword.emailFrom = System.getenv("MC_MAIL_FROM")

    if (System.getenv("MC_MAIL_HOST")) {
        grails {
            mail {
                host = System.getenv("MC_MAIL_HOST")

                if (System.getenv("MC_MAIL_PORT")) {
                    port = System.getenv("MC_MAIL_PORT") as Integer
                }

                if (System.getenv("MC_MAIL_USERNAME")) {
                    username = System.getenv("MC_MAIL_USERNAME")
                }

                if (System.getenv("MC_MAIL_PASSWORD")) {
                    password = System.getenv("MC_MAIL_PASSWORD")
                }

                if (System.getenv("MC_MAIL_PROPS")) {
                    props = new JsonSlurper().parseText(System.getenv("MC_MAIL_PROPS"))
                }

            }
        }
    }
}

if (System.getenv("MC_NAME")) {
    mc.name = System.getenv("MC_NAME")
}

if (System.getenv("MC_WELCOME_JUMBO")) {
    mc.welcome.jumbo = System.getenv("MC_WELCOME_JUMBO")
}

if (System.getenv("MC_WELCOME_INFO")) {
    mc.welcome.info = System.getenv("MC_WELCOME_INFO")
}

if (System.getenv("MC_ALLOW_SIGNUP")) {
    mc.allow.signup = true
}

if (System.getenv("MC_GOOGLE_KEY")) {
    oauth {
        providers {
            google {
                // this key is limited to localhost only so no need to hide it
                api = org.modelcatalogue.repack.org.scribe.builder.api.GoogleApi20
                key = System.getenv('MC_GOOGLE_KEY')
                secret = System.getenv('MC_GOOGLE_SECRET')
                successUri = '/oauth/google/success'
                failureUri = '/oauth/google/error'
                callback = "${grails.serverURL}/oauth/google/callback"
                scope = 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email'
            }
        }
    }
}

if (System.getenv('MC_S3_BUCKET')) {
    mc.storage.s3.key = System.getenv('MC_S3_KEY') ?: System.getenv('AWS_ACCESS_KEY_ID')
    mc.storage.s3.secret = System.getenv('MC_S3_SECRET') ?: System.getenv('AWS_SECRET_KEY')
    mc.storage.s3.region = System.getenv('MC_S3_REGION')
    mc.storage.s3.bucket = System.getenv('MC_S3_BUCKET')
}

if (System.getenv('MC_SECURED_REVERSE_PROXY')) {
    // Setting of https behind load balancer (or proxy server) needs to set http header 'X-Forwarded-Proto' in order
    // to decide if http or https should be used. Environment without load balancer is not affected.
    grails.plugin.springsecurity.secureChannel.useHeaderCheckChannelSecurity = true
    grails.plugin.springsecurity.portMapper.httpPort = 80
    grails.plugin.springsecurity.portMapper.httpsPort = 443
    grails.plugin.springsecurity.secureChannel.secureHeaderName = 'X-Forwarded-Proto'
    grails.plugin.springsecurity.secureChannel.secureHeaderValue = 'http'
    grails.plugin.springsecurity.secureChannel.insecureHeaderName = 'X-Forwarded-Proto'
    grails.plugin.springsecurity.secureChannel.insecureHeaderValue = 'https'
    grails.plugin.springsecurity.secureChannel.definition = [
        [pattern: '/**', access: 'REQUIRES_SECURE_CHANNEL']
    ]
}

if (System.getenv("MC_CSS_CUSTOM")) {
    mc.css.custom = System.getenv("MC_CSS_CUSTOM")
}

if (System.getenv('MC_MAX_ACTIVE_USERS')) {
    mc.max.active.users = System.getenv('MC_MAX_ACTIVE_USERS')
}

if (System.getenv('MC_PRELOAD')) {
    try {
        mc.preload = new JsonSlurper().parseText(System.getenv('MC_PRELOAD'))
    } catch (ignored) {}
}

