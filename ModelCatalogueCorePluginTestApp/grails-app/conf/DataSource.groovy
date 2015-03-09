dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache    = true
    cache.use_query_cache           = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    test {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    local {
        dataSource {
            dbCreate = "create"
            url = "jdbc:mysql://localhost:3306/${System.getProperty('mc.db.schema') ?: System.getenv('METADATA_DB_SCHEMA') ?: 'nhic'}?autoReconnect=true&useUnicode=yes"
            username = System.getenv('METADATA_DB_USERNAME')
            password = System.getenv('METADATA_DB_PASSWORD')
            driverClassName = "com.mysql.jdbc.Driver"
        }
    }
    cloudbees {
        dataSource {
            dbCreate = ""
            url = "jdbc:mysql://ec2-176-34-253-124.eu-west-1.compute.amazonaws.com:3306/modelcatalogue-core-testapp?autoReconnect=true&useUnicode=yes"
            username = System.getenv('METADATA_DB_USERNAME')
            password = System.getenv('METADATA_DB_PASSWORD')
            driverClassName = "com.mysql.jdbc.Driver"
            logSql = true
        }
    }
    production {
        dataSource {
            driverClassName = "com.mysql.jdbc.Driver"
            url = System.getenv('METADATA_DB_NAME') ?: "jdbc:mysql://localhost:3306/${System.getProperty('mc.db.schema') ?: System.getenv('METADATA_DB_SCHEMA') ?: 'nhic'}?autoReconnect=true&useUnicode=yes"
            username = System.getenv('METADATA_DB_USERNAME')
            password = System.getenv('METADATA_DB_PASSWORD')
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

    }
}
