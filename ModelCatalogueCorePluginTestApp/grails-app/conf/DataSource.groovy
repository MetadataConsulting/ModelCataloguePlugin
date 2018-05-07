hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    //    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
//    documentCache.region.factory_class = 'org.hibernate.documentCache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
    // XXX: never commit your local configuration overrides for this file!!!
    development {
        dataSource {
            pooled = true
            driverClassName = System.getenv('MDX_DB_DRIVER') ?: 'org.h2.Driver'
            if (System.getenv('MC_JDBC_DRIVER') == 'com.mysql.jdbc.Driver') {
                dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
            }
            url = System.getenv('MDX_DB_URL') ?: "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
            username = System.getenv('MDX_DB_USERNAME') ?: 'sa'
            password = System.getenv('MDX_DB_PASSWORD') ?: ''
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
        }
    }
    test {
        if (System.getenv('DOCKERIZED_TESTS') && System.properties["grails.test.phase"] == 'functional') {
            dataSource {
                driverClassName = "com.mysql.jdbc.Driver"
                dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
                url = "jdbc:mysql://${System.getenv('MC_DOCKER_HOST') ?: 'localhost'}:43306/tester?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
                username = 'root'
                password = 'pa$$w0rd'
                dbCreate = "create-drop"
                properties {
                    maxActive = -1
                    minEvictableIdleTimeMillis = 1800000
                    timeBetweenEvictionRunsMillis = 1800000
                    numTestsPerEvictionRun = 3
                    testOnBorrow = true
                    testWhileIdle = true
                    testOnReturn = false
                    validationQuery = "SELECT 1"
                    jdbcInterceptors = "ConnectionState"
                }
            }
        } else {
            dataSource {
                driverClassName = "com.mysql.jdbc.Driver"
                dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'
                url = "jdbc:mysql://localhost:3306/tester?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
                username = 'root'
                password = 'nextdefault'
                dbCreate = "update"
                properties {
                    maxActive = -1
                    minEvictableIdleTimeMillis = 1800000
                    timeBetweenEvictionRunsMillis = 1800000
                    numTestsPerEvictionRun = 3
                    testOnBorrow = true
                    testWhileIdle = true
                    testOnReturn = false
                    validationQuery = "SELECT 1"
                    jdbcInterceptors = "ConnectionState"
                }
            }
        }
    }
    production {
        dataSource {
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = 'org.hibernate.dialect.MySQL5InnoDBDialect'

            url = System.getenv('MDX_DB_URL')//"jdbc:mysql://localhost:3306/exceltest"
            username = System.getenv('MDX_DB_USERNAME')//'mdradmin'
            password = System.getenv('MDX_DB_PASSWORD')//'mdradmin123'
            dbCreate = "update"
            properties {
                maxActive = -1
                minEvictableIdleTimeMillis = 1800000
                timeBetweenEvictionRunsMillis = 1800000
                numTestsPerEvictionRun = 3
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                validationQuery = "SELECT 1"
                jdbcInterceptors = "ConnectionState"
            }
        }
    }
}
