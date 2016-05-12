#!/usr/bin/env bash

source docker.sh

if [[ -z "$1" ]]; then
    echo "Usage: elasticsearch.sh <stop|start|rm>"
    exit 1
fi

ES_VERSION="2.1.2"
KIBANA_VERSION="4.3.1"
ES_PORT="9200"

if [ "$1" = "start" ] ; then
    echo -e "Starting ElasticSearch v$ES_VERSION instance with docker.\nIf you have upgraded the version of your client, you should upgrade the version in this script as well.\nInitializing containers may take a while, please be patient."
    docker_start_or_run mc-es -p 9200:9200 -p 9300:9300 elasticsearch:"$ES_VERSION" -Dnetwork.host=0.0.0.0 -Des.threadpool.bulk.queue_size=3000
    docker_start_or_run mc-kibana -e "ELASTICSEARCH_URL=http://$DOCKER_MACHINE_IP:9200" -p 5601:5601 kibana:"$KIBANA_VERSION"
    docker_exec mc-kibana kibana plugin --install elastic/sense
    docker_restart mc-kibana
    echo -e "ElasticSearch v$ES_VERSION started at http://$DOCKER_MACHINE_IP:9200.\nUse $DOCKER_MACHINE_IP:9300 to connect to with the Java client.\nYou can also use Kibana dashboard at http://$DOCKER_MACHINE_IP:5601.\nDocker IP $DOCKER_MACHINE_IP stored in '.docker-ip' file for reference from the scripts.\n"
    write_docker_file
elif [ "$1" = "stop" ] ; then
    echo -e "Stopping ElasticSearch v$ES_VERSION instance with docker.\nIf you have upgraded the version of your client, you should upgrade the version is this script as well.\n"
    docker_stop mc-es
    docker_stop mc-kibana
    echo -e "ElasticSearch v$ES_VERSION is no longer available at http://$DOCKER_MACHINE_IP:9200"
    remove_docker_file
elif [ "$1" = "rm" ] ; then
    echo -e "Removing containers for ElasticSearch v$ES_VERSION instance with docker."
    docker_rm mc-es
    docker_rm mc-kibana
    echo -e "ElasticSearch v$ES_VERSION is no longer available at http://$DOCKER_MACHINE_IP:9200.\nContainers were removed as well.\n"
    remove_docker_file
fi

