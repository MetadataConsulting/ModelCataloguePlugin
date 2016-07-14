# Running on Amazon Web Services

As Model Catalogue is shipped as Docker image it can be easily run using
[Amazon Elastic Container Service](https://eu-west-1.console.aws.amazon.com/ecs/home?region=eu-west-1).


## Setup

### Amazon S3
You need a Amazon S3 bucket to store assets into.

### Amazon RSD
You need one `db.t2.small` MySQL instance running. The security group
must allow inbound connections from the ECS registered instance (see bellow).

### ECS Cluster
You need _Cluster_ available with enough registered
container instances. There is _Cluster_ called _default_ always available
but you need to start new Amazon ESC container instance for it if not
set up yet. Follow the guide here: [Amazon ECS Container Instances](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_instances.html).
The instance should be `m3.xlarge` to allow running Model Catalogue with
enough memory. If you've setup the instance properly it should appear
at _Clusters_ / _ESC Instances_ table.

### Task Definitions
Task definition are quite similar to `Dockerfile` or `docker-compose.yml` files.
They specify the containers to be run with some additional settings such as
environmental variables. You need two task definitions:

  * Model Catalogue container based on [metadata/registry-elasticsearch](https://hub.docker.com/r/metadata/registry/)
  * Elasticsearch container based on [metadata/registry-elasticsearch](https://hub.docker.com/r/metadata/registry-elasticsearch/)

#### Elasticsearch Task Definition
You can easily create new _Task Definition_ using following JSON definition
if you go to _Task Definitions_ / _Crate new Task Definition / _Configure via JSON_.

```
{
    "containerDefinitions": [
        {
            "volumesFrom": [],
            "memory": 4000,
            "extraHosts": null,
            "dnsServers": null,
            "disableNetworking": null,
            "dnsSearchDomains": null,
            "portMappings": [
                {
                    "hostPort": 9300,
                    "containerPort": 9300,
                    "protocol": "tcp"
                }
            ],
            "hostname": null,
            "essential": true,
            "entryPoint": null,
            "mountPoints": [
                {
                    "containerPath": "/usr/share/elasticsearch/data",
                    "sourceVolume": "mc-es-data",
                    "readOnly": null
                }
            ],
            "name": "mc-es",
            "ulimits": null,
            "dockerSecurityOptions": null,
            "environment": [],
            "links": null,
            "workingDirectory": null,
            "readonlyRootFilesystem": null,
            "image": "metadata/registry-elasticsearch:2",
            "command": [],
            "user": null,
            "dockerLabels": null,
            "logConfiguration": {
                "logDriver": "json-file",
                "options": null
            },
            "cpu": 1024,
            "privileged": null
        }
    ],
    "volumes": [
        {
            "host": {
                "sourcePath": "/opt/docker-volumes/mc-es-data"
            },
            "name": "mc-es-data"
        }
    ],
    "family": "your-mc-es-task-definition-name"
}
```

You need to update the _Task Definition Name_ after saving the
JSON configuration to something more meaningful than `your-mc-es-task-definition-name`
and click `create`.

#### Model Catalogue Task Definition

```
{
    "taskRoleArn": null,
    "containerDefinitions": [
        {
            "volumesFrom": [],
            "memory": 10240,
            "extraHosts": null,
            "dnsServers": null,
            "disableNetworking": null,
            "dnsSearchDomains": null,
            "portMappings": [
                {
                    "hostPort": 80,
                    "containerPort": 8080,
                    "protocol": "tcp"
                }
            ],
            "hostname": null,
            "essential": true,
            "entryPoint": null,
            "mountPoints": [],
            "name": "mc",
            "ulimits": null,
            "dockerSecurityOptions": null,
            "environment": [
                {
                    "name": "MC_ALLOW_SIGNUP",
                    "value": "true"
                },
                {
                    "name": "MC_MAIL_USERNAME",
                    "value": "postmaster@example.com"
                },
                {
                    "name": "MC_MAIL_PASSWORD",
                    "value": "MAILSERVER_PWD"
                },
                {
                    "name": "MC_MAIL_PORT",
                    "value": "587"
                },
                {
                    "name": "MC_MAIL_HOST",
                    "value": "mx.example.com"
                },
                {
                    "name": "MC_NAME",
                    "value": "My Model Catalogue"
                },
                {
                    "name": "CATALINA_OPTS",
                    "value": "-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms2g -Xmx10g -XX:NewSize=2048m -XX:MaxNewSize=2048m -XX:PermSize=2048m -XX:MaxPermSize=2048m -XX:+DisableExplicitGC"
                },
                {
                    "name": "METADATA_JDBC_URL",
                    "value": "jdbc:mysql://<rds url>/<database name>?autoReconnect=true&useUnicode=yes&encoding=UTF-8"
                },
                {
                    "name": "METADATA_USERNAME",
                    "value": "RDS_USERNAME"
                },
                {
                    "name": "METADATA_PASSWORD",
                    "value": "RDS_PASSWORD"
                },
                {
                    "name": "MC_ES_HOST",
                    "value": "MC_ES_HOST"
                },
                {
                    "name": "MC_S3_SECRET",
                    "value": "SECRET-TO-S3"
                },
                {
                    "name": "MC_S3_BUCKET",
                    "value": "S3-BUCKET_NAME"
                },
                {
                    "name": "MC_S3_KEY",
                    "value": "KEY-TO-S3"
                },
                {
                    "name": "MC_ES_ELEMENTS_PER_BATCH",
                    "value": "20"
                },
                {
                    "name": "MC_ES_DELAY_AFTER_BATCH",
                    "value": "200"
                },
                {
                    "name": "METADATA_HOST",
                    "value": "yourmchost.example.com"
                }
            ],
            "links": [],
            "workingDirectory": null,
            "readonlyRootFilesystem": null,
            "image": "metadata/registry:2",
            "command": null,
            "user": null,
            "dockerLabels": null,
            "logConfiguration": {
                "logDriver": "syslog",
                "options": null
            },
            "cpu": 2048,
            "privileged": null
        }
    ],
    "volumes": [],
    "family": "your-mc-task-definition-name"
}
```

