ElasticSearch Search Support
====================
Version 2 supports the ElasticSearch for searching catalogue elements. You can enable ElasticSearch in `mc-config.groovy` configuration file. For a first time you setup ElasticSearch for given server you need to run `Reindex Catalogue` global admin action (`Ctrl+Space` + `Reindex Catalogue`).

## Running with ElasticSearch in Production

### Configuring Remote Instance

Running ElasticSearch on separate server is preferred. You have to put
the hostname and optionally the port to the configuration:

```java
mc.search.elasticsearch.host = "192.168.1.123"
// ElasticSearch bind port, defaults to 9300
mc.search.elasticsearch.port = "9300"
```

You can also specify the host and the port in the command line using Java properties of the same names on command-line: 

`./grailsw run-app -Dmc.search.elasticsearch.host=192.168.1.123`

In Tomcat you can put these properties in `catalina.properties` as well.

If you need additional settings such as cluster name you can configure
the ElasticSearch server using a closure assigned to 'mc.search.elasticsearch.settings'

```java
mc.search.elasticsearch.settings = {
    put 'some.value', 100
}
```

The closure delegates to [Settings.Builder](https://github.com/elastic/elasticsearch/blob/master/core/src/main/java/org/elasticsearch/common/settings/Settings.java) class.

### Configuring Local Instance


Local instance of ElasticSearch runs in the same JVM and requires the writable data directory to be configured:

```java
mc.search.elasticsearch.local = "/full/path/to/data/dir"
```

## Running with ElasticSearch Locally

There is new `./elasticsearch.sh` script in the root of the project folder which launches [ElasticSearch](https://www.elastic.co/) with [Kibana](https://www.elastic.co/products/kibana)
and [Sense](https://github.com/elastic/sense). The scripts accepts three commands `start`, `stop` and `rm`. You need to have [Docker Toolbox](https://www.docker.com/docker-toolbox) installed to run this command.

### Starting the Local ElasticSearch Instance

Running `./elasticsearch.sh start` will launch all required Docker containers and store the Docker IP in `.docker-ip`  file from which the `./run-prod.sh` command will get it automatically and start with the proper settings.
```
$ ./elasticsearch.sh start

Starting ElasticSearch v2.0.0 instance with docker.
If you have upgraded the version of your client, you should upgrade the version is this script as well.

ElasticSearch v2.0.0 started at http://192.168.99.100:9200.
Use 192.168.99.100:9300 to connect to with the Java client.
You can also use Kibana dashboard at http://192.168.99.100:5601.
Docker IP 192.168.99.100 stored in '.docker-ip' file for reference from the scripts.

# runs with ElasticSearch enabled automatically
$ ./run-prod.sh
```

You can see the links to [Kibana](https://www.elastic.co/products/kibana) as well. [Kibana](https://www.elastic.co/products/kibana) is great tool to inspect
your indices (it's phpMyAdmin of ElasticSearch :smile:).  [Sense](https://github.com/elastic/sense) is installed there as well and it's the best way how to interact with ElasticSearch server with JSON REST API as described in the  [ElasticSearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html). You can even set the URL of your  [Sense](https://github.com/elastic/sense) for the [ElasticSearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html) examples.

### Stopping the Local ElasticSearch Instance
Running `./elasticsearch.sh stop` will stop all required Docker containers and remove the Docker IP in `.docker-ip`  file so the `./run-prod.sh` command will no longer get it automatically and start with default settings.

### Removing the Local ElasticSearch Instances
Run `./elasticsearch.sh stop` to reset all changes in the local ElasticSearch servers.










