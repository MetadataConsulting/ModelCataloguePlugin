// config
grails.logging.jul.usebridge = false
grails.serverURL =  "http://${System.getenv('VIRTUAL_HOST') ?: System.getenv('METADATA_HOST') ?: System.getenv('DOCKER_MACHINE_IP') ?: new URL("http://checkip.amazonaws.com").text.trim()}"

// datasource
dataSource {

    def metadataDbPassword = System.getenv('METADATA_PASSWORD') ?: System.getenv('MC_MYSQL_ENV_MYSQL_PASSWORD') ?: 'metadata'
    def metadataDbUsername = System.getenv('METADATA_USERNAME') ?: System.getenv('MC_MYSQL_ENV_MYSQL_USER') ?: 'metadata'
    def metadataJdbcString = System.getenv('METADATA_JDBC_URL')

    if (!metadataJdbcString && System.getenv("MC_MYSQL_NAME")) {
        metadataJdbcString = "jdbc:mysql://mc-mysql:3306/${System.getenv('MC_MYSQL_ENV_MYSQL_DATABASE') ?: 'metadata'}?autoReconnect=true&useUnicode=yes"
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