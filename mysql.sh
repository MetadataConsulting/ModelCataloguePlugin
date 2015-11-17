#!/usr/bin/env bash

source docker.sh

if [[ -z "$1" ]]; then
    echo "Usage: mysql.sh <stop|start|rm>"
    exit 1
fi

MYSQL_VERSION="5.7"
MYSQL_PORT="3306"

if [ "$1" = "start" ] ; then
    echo -e "Starting MySQL v$MYSQL_VERSION instance with docker.\nInitializing containers may take a while, please be patient."
    docker_start_or_run mc-mysql -e MYSQL_ROOT_PASSWORD=thepassword -e MYSQL_DATABASE=metadata -e MYSQL_USER=metadata -e MYSQL_PASSWORD=metadata -p "$MYSQL_PORT":"$MYSQL_PORT" mysql:"$MYSQL_VERSION"
    echo -e "MySQL v$MYSQL_VERSION started at http://$DOCKER_MACHINE_IP:$MYSQL_PORT. Default database is metadata. Default credentials are metadata:metadata"
    write_docker_file
elif [ "$1" = "stop" ] ; then
    echo -e "Stopping MySQL v$MYSQL_VERSION instance with docker."
    docker_stop mc-mysql
    echo -e "MySQL v$MYSQL_VERSION is no longer available at http://$DOCKER_MACHINE_IP:$MYSQL_PORT"
    remove_docker_file
elif [ "$1" = "rm" ] ; then
    echo -e "Removing containers for MySQL v$MYSQL_VERSION instance with docker."
    docker_rm mc-mysql
    echo -e "MySQL v$MYSQL_VERSION is no longer available at http://$DOCKER_MACHINE_IP:$MYSQL_PORT.\nContainers were removed as well."
    remove_docker_file
fi

