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

  1. Model Catalogue container based on [metadata/registry-elasticsearch](https://hub.docker.com/r/metadata/registry/)
  2. Elasticsearch container based on [metadata/registry-elasticsearch](https://hub.docker.com/r/metadata/registry-elasticsearch/)

You can easily create new _Task Definition_ using following JSON definition
if you go to _Task Definitions_ / _Crate new Task Definition / _Configure via JSON_.


```
{
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
                    "value": "c00lpwd"
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
                    "value": "Model Catalogue"
                },
                {
                    "name": "CATALINA_OPTS",
                    "value": "-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms4g -Xmx10g -XX:NewSize=2048m -XX:MaxNewSize=2048m -XX:PermSize=2048m -XX:MaxPermSize=2048m -XX:+DisableExplicitGC"
                },
                {
                    "name": "METADATA_JDBC_URL",
                    "value": "jdbc:mysql://your-rds-database.rds.amazonaws.com/your-database?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8"
                },
                {
                    "name": "METADATA_USERNAME",
                    "value": "username"
                },
                {
                    "name": "METADATA_PASSWORD",
                    "value": "strongpwd"
                },
                {
                    "name": "MC_S3_SECRET",
                    "value": "S3CR3T"
                },
                {
                    "name": "MC_S3_BUCKET",
                    "value": "mc.assets.example.com"
                },
                {
                    "name": "MC_S3_KEY",
                    "value": "THEKEY"
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
                    "value": "my-catalogue.example.com"
                }
            ],
            "links": [
                "mc-es:mc-es"
            ],
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
        },
        {
            "volumesFrom": [],
            "memory": 4096,
            "extraHosts": null,
            "dnsServers": null,
            "disableNetworking": null,
            "dnsSearchDomains": null,
            "portMappings": [],
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
    "family": "your-mc-all-task-definition"
}
```

You need to update the _Task Definition Name_ after saving the
JSON configuration to something more meaningful than `your-mc-all-task-definition-name`
and click `create`.

You need to update environmental variables to reflect your own settings
such as mail server, database host and credentials or catalogue URL.
See the [environmental variables description](environment.adoc)

The Elasticsearch indicies are considered ephemeral yet they are persisted
 in separate volume `/opt/docker-volumes/mc-es-data` between container
 restarts. If the indicies are lost they can always be recreated using
 `Reindex Catalogue` admin action of the Model Catalogue from the data
 stored in the database.

### Services
When you have your _Task Definition_ ready you can _Create_ new service
based on it. Call it for example `mc` and set the minimal tasks number
to one.

## Model Catalogue Upgrade
There are several way how to keep your Model Catalogue up to date.
One of them is to always point to specific version such e.g.
`metadata/registry:2.0.0-beta-10` instead of just `metadata/registry:2`
in your task definition. If you want to update to newer version then
create new _Task Definition Revision_ change the version tag and restart
the service with the new revision as described bellow.

If you want to use the latest `metadata/registry:2` then you have to
SSH into the container instance and pull the latest version manually.

```
ssh -i ~/.ssh/ec2containers.pem ec2-user@ec2-1-2-3-4.eu-west-1.compute.amazonaws.com
docker pull metadata/registry:2
```

Restart the service using following steps:

  1. Update the Model Catalogue service (`mc` in previous example) and set the number of tasks required to `0`
  2. Wait until the task is stopped
  3. Update the Model Catalogue service (`mc` in previous example) and set the number of tasks required to `1`


