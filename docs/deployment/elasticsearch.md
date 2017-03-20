Elasticsearch Search Support
====================
Version 2 supports the Elasticsearch for searching catalogue elements. You can enable Elasticsearch in `mc-config.groovy` configuration file. For a first time you setup Elasticsearch for given server you need to run `Reindex Catalogue` global admin action (`Ctrl+Space` + `Reindex Catalogue`).

## Running with Elasticsearch in Production

### Configuring Remote Instance

Running Elasticsearch on separate server is preferred. You have to put
the hostname and optionally the port to the configuration:

```
mc.search.elasticsearch.host = "192.168.1.123"
// Elasticsearch bind port, defaults to 9300
mc.search.elasticsearch.port = "9300"
```

You can also specify the host and the port in the command line using Java properties of the same names on command-line:

`./gradlew -Pmc.search.elasticsearch.host=192.168.1.123`

In Tomcat you can put these properties in `catalina.properties` as well.

If you need additional settings such as cluster name you can configure
the Elasticsearch server using a closure assigned to 'mc.search.elasticsearch.settings'

```
mc.search.elasticsearch.settings = {
    put 'some.value', 100
}
```

The closure delegates to [Settings.Builder](https://github.com/elastic/elasticsearch/blob/master/core/src/main/java/org/elasticsearch/common/settings/Settings.java) class.

### Configuring Local Instance


Local instance of Elasticsearch runs in the same JVM and requires the writable data directory to be configured:

```
mc.search.elasticsearch.local = "/full/path/to/data/dir"
```

## Running with Elasticsearch Locally

There is new `./elasticsearch.sh` script in the root of the project folder which launches [Elasticsearch](https://www.elastic.co/) with [Kibana](https://www.elastic.co/products/kibana)
and [Sense](https://github.com/elastic/sense). The scripts accepts three commands `start`, `stop` and `rm`. You need to have [Docker Toolbox](https://www.docker.com/docker-toolbox) installed to run this command.

Running with local Elasticsearch instance not recommended as it often causes memory crashes!

## Running with Docker Container
 
Gradle script comes with two tasks to start and stop preconfigured Elasticsearch instance
in Docker:
*  `startElasticsearchContainer`
*  `stopElasticsearchContainer`

Application then can connect to it using following host and port: `<docker host>:49300`.

When you launch your applicaiton with `./gradlew runProd` this instance in Docker is used by default without any additional configuration needed.








