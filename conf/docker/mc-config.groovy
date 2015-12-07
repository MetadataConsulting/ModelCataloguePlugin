// config
grails.logging.jul.usebridge = false
grails.serverURL =  "http://${System.getenv('VIRTUAL_HOST')}"

// datasource
dataSource {
    driverClassName = "com.mysql.jdbc.Driver"
    dialect='org.hibernate.dialect.MySQL5InnoDBDialect'
    url = "jdbc:mysql://mc-mysql:3306/metadata?autoReconnect=true&useUnicode=yes"
    username = "metadata"
    password = "metadata"
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
}

mc.search.elasticsearch.host='mc-es'

grails.plugin.console.enabled=true

mc.legacy.dataModels=true

// unless we find out the way how to preload the database, it doesn't make sense to migrate automatically
// grails.plugin.databasemigration.updateOnStart=true
// grails.plugin.databasemigration.updateOnStartFileNames=["changelog.groovy"]