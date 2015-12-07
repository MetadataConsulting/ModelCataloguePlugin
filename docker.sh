#!/usr/bin/env bash

AVAILABLE_CORES=$(grep -c ^processor /proc/cpuinfo 2>/dev/null || sysctl -n hw.ncpu)
HALF_OF_CPU=$((AVAILABLE_CORES * 100000 / 2))

if [ "$(uname)" == "Darwin" ] ||  [ "$(expr substr $(uname -s) 1 10)" == "MINGW32_NT" ] ; then
    # you can have multiple docker machines on your computer, default should present after installation
    : ${MC_DOCKER_MACHINE_ENV:="default"}

    command -v docker-machine >/dev/null 2>&1 || { echo "Docker Machine is required to run this script.  Please, install it from https://www.docker.com/docker-toolbox." >&2; exit 1; }

    docker-machine start "$MC_DOCKER_MACHINE_ENV" &>/dev/null

    eval "$(docker-machine env "$MC_DOCKER_MACHINE_ENV")"

    # only reliable variable on every operation system
    DOCKER_MACHINE_IP=$(docker-machine ip "$MC_DOCKER_MACHINE_ENV")

    # to be able to connect with simple docker command
    export DOCKER_MACHINE_IP
    export DOCKER_TLS_VERIFY
    export DOCKER_HOST
    export DOCKER_CERT_PATH
    export DOCKER_MACHINE_NAME

elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    DOCKER_MACHINE_IP=$(dig +short myip.opendns.com @resolver1.opendns.com || ip route get 8.8.8.8 | head -1 | cut -d' ' -f8 || ip a s|sed -ne '/127.0.0.1/!{s/^[ \t]*inet[ \t]*\([0-9.]\+\)\/.*$/\1/p}')
fi

function docker_exec() {
    local NAME=$1
    shift
    docker exec -it "$NAME" "$@"
}

function docker_restart() {
    docker stop "$1" && docker start "$1"
}

function docker_stop() {
    docker stop "$1"
}

function docker_rm() {
    docker rm -f "$1"
}

function docker_start_or_run() {
    local NAME=$1
    shift
    docker start "$NAME"  &>/dev/null || docker run --restart=always --cpu-quota="$HALF_OF_CPU" -d --name="$NAME" "$@"
}

function write_docker_file() {
    echo "$DOCKER_MACHINE_IP" > .docker-ip
}

function remove_docker_file() {
    rm .docker-ip
}
