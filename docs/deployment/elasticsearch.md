Elasticsearch Search Support
====================
Version 2 supports Elasticsearch for searching catalogue elements. You can enable Elasticsearch in the `mc-config.groovy` configuration file. The first time you setup Elasticsearch for a given server you need to run the `Reindex Catalogue` global admin action (`Ctrl+Space` + `Reindex Catalogue`).

## Running with Elasticsearch in Production

### Configuring a Remote Instance

Running Elasticsearch on a separate server to the catalogue is preferred. There are various ways of telling the catalogue where to find it.
- You can put the hostname, and optionally the port, in `mc-config.groovy`:

```
mc.search.elasticsearch.host = "192.168.1.123"
// Elasticsearch bind port, defaults to 9300
mc.search.elasticsearch.port = "9300"
```

- You can also specify the host and port in the command line using Java properties of the same names on the command-line:

```./gradlew -Pmc.search.elasticsearch.host=192.168.1.123```

- In Tomcat you can put these properties in `catalina.properties` as well.

If you need to configure additional Elasticsearch settings, such as cluster name, you can do this using a closure assigned to 'mc.search.elasticsearch.settings' in `mc-config.groovy`:

```
mc.search.elasticsearch.settings = {
    put 'some.value', 100
}
```

The closure delegates to the [Settings.Builder](https://github.com/elastic/elasticsearch/blob/master/core/src/main/java/org/elasticsearch/common/settings/Settings.java) class.

### Configuring a Local Instance

A local instance of Elasticsearch will in the same JVM and requires a writable data directory to be configured:

```
mc.search.elasticsearch.local = "/full/path/to/data/dir"
```

## Running with Elasticsearch Locally

There is new `./elasticsearch.sh` script in the root of the project folder which launches [Elasticsearch](https://www.elastic.co/) with [Kibana](https://www.elastic.co/products/kibana)
and [Sense](https://github.com/elastic/sense). The script accepts three commands: `start`, `stop` and `rm`. You need to have [Docker Toolbox](https://www.docker.com/docker-toolbox) installed to run this command.

Running a local Elasticsearch instance not recommended as it often causes memory crashes!

## Running with Docker Container
 
Gradle script comes with two tasks to start and stop a preconfigured Elasticsearch instance
in Docker:
*  `startElasticsearchContainer`
*  `stopElasticsearchContainer`

The application then can connect to it using following host and port: `<docker host>:49300`.

When you launch your applicaiton with `./gradlew runProd`, this instance in Docker is used by default without any additional configuration needed.








