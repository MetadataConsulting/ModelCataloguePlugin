hibernate {
    cache.use_second_level_cache    = true
    cache.use_query_cache           = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    documentCache.region.factory_class = 'org.hibernate.documentCache.ehcache.EhCacheRegionFactory' // Hibernate 4
//        show_sql=true
}

// environment specific settings
environments {
    development {
        dataSource {
            pooled = true
            driverClassName = "com.mysql.jdbc.Driver"
//            url = "jdbc:mysql://localhost:3306/G26m"
//            url = "jdbc:mysql://localhost:3306/mctest"
            url = "jdbc:mysql://localhost:3306/ebdb"
//            url = "jdbc:mysql://localhost:3306/admrdfinal1"
//            url = "jdbc:mysql://localhost:3306/admfinal1"
            username = "root"
            password = "admin123"
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
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
    }
    test {
        dataSource {
            pooled = true
            driverClassName = "org.h2.Driver"
            username = "sa"
            password = ""
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        // from external config
    }
}
