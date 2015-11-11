#!/usr/bin/env bash

if [[ -z "$1" ]]; then
    echo "Usage: elasticsearch.sh <stop|start|rm>"
    exit 1
fi

ES_VERSION="2.0.0"
ES_PORT=9200
DOCKER_MACHINE_ENV="default"


command -v docker-machine >/dev/null 2>&1 || { echo "Docker Machine is required to run this script.  Please, install it from https://www.docker.com/docker-toolbox." >&2; exit 1; }

docker-machine start "$DOCKER_MACHINE_ENV"

eval "$(docker-machine env default)"

DOCKER_MACHINE_IP=$(docker-machine ip default)

if [ "$1" = "start" ] ; then
    echo "Starts ElasticSearch v$ES_VERSION instance with docker. If you have upgraded the version of your client, you should upgrade the version is this script as well."
    docker start mc-es || docker run -d --name=mc-es -p 9200:9200 -p 9300:9300 elasticsearch:"$ES_VERSION" -Dnetwork.host=0.0.0.0
    docker start mc-kibana || docker run --name=mc-kibana -e "ELASTICSEARCH_URL=http://$DOCKER_MACHINE_IP:9200" -p 5601:5601 -d kibana
    echo "ElasticSearch v$ES_VERSION started at http://$DOCKER_MACHINE_IP:9200. Use $DOCKER_MACHINE_IP:9300 to connect to with the Java client. You can also use Kibana dashboard at http://$DOCKER_MACHINE_IP:5601."
elif [ "$1" = "stop" ] ; then
    echo "Stops ElasticSearch v$ES_VERSION instance with docker. If you have upgraded the version of your client, you should upgrade the version is this script as well."
    docker stop mc-es
    docker stop mc-kibana
    echo "ElasticSearch v$ES_VERSION is no longer available at http://$DOCKER_MACHINE_IP:9200"
elif [ "$1" = "rm" ] ; then
    echo "Removes containers for ElasticSearch v$ES_VERSION instance with docker."
    docker rm -f mc-es
    docker rm -f mc-kibana
    echo "ElasticSearch v$ES_VERSION is no longer available at http://$DOCKER_MACHINE_IP:9200. Containers were removed as well."
fi

