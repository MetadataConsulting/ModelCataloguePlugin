#!/usr/bin/env bash

# you can have multiple docker machines on your computer, default should present after installation
: ${MC_DOCKER_MACHINE_ENV:="default"}

command -v docker-machine >/dev/null 2>&1 || { echo "Docker Machine is required to run this script.  Please, install it from https://www.docker.com/docker-toolbox." >&2; exit 1; }

docker-machine start "$MC_DOCKER_MACHINE_ENV" &>/dev/null

eval "$(docker-machine env "$MC_DOCKER_MACHINE_ENV")"

DOCKER_MACHINE_IP=$(docker-machine ip "$MC_DOCKER_MACHINE_ENV")

export MC_DOCKER_MACHINE_ENV
export DOCKER_MACHINE_IP
export DOCKER_TLS_VERIFY
export DOCKER_HOST
export DOCKER_CERT_PATH
export DOCKER_MACHINE_NAME

function docker_exec() {
    local NAME=$1
    shift
    docker exec -it "$NAME" "$@"  &>/dev/null
}

function docker_restart() {
    docker stop "$1" && docker start "$1" &>/dev/null
}

function docker_stop() {
    docker stop "$1" &>/dev/null
}

function docker_rm() {
    docker rm -f "$1" &>/dev/null
}

function docker_start_or_run() {
    local NAME=$1
    shift
    docker start "$NAME"  &>/dev/null || docker run -d --name="$NAME" "$@" &>/dev/null
}

function write_docker_file() {
    echo "$DOCKER_MACHINE_IP" > .docker-ip
}

function remove_docker_file() {
    rm .docker-ip &>/dev/null
}
