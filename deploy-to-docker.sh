#!/usr/bin/env bash

source docker.sh

./mysql.sh start
./elasticsearch.sh start

: ${TOMCAT_VERSION:="8"}
: ${MYSQL_CONTAINER:="mc-mysql"}
: ${ES_CONTAINER:="mc-es"}
: ${MC_VERSION:="2.0.0"}


if ! test -f "build/ModelCatalogueCorePluginTestApp-$MC_VERSION.war" ; then
    ./war.sh
fi

echo -e "Starting Tomcat v$TOMCAT_VERSION instance with docker.\nInitializing containers may take a while, please be patient."

if [[ "$1" ]]; then
    MC_TOMCAT_HOST="$1"
    docker_start_or_run router -d -p 80:80 -p 443:443 -v /var/run/docker.sock:/tmp/docker.sock:ro jwilder/nginx-proxy
    docker_start_or_run mc-tomcat -e VIRTUAL_HOST="$MC_TOMCAT_HOST" -e VIRTUAL_PORT=8080 -e CATALINA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1g -Xmx2g -XX:NewSize=512m -XX:MaxNewSize=512m -XX:PermSize=512m -XX:MaxPermSize=512m -XX:+DisableExplicitGC" --link "$MYSQL_CONTAINER":mc-mysql --link "$ES_CONTAINER":mc-es tomcat:"$TOMCAT_VERSION"
else
    docker_start_or_run mc-tomcat -p 80:8080 -e CATALINA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1g -Xmx2g -XX:NewSize=512m -XX:MaxNewSize=512m -XX:PermSize=512m -XX:MaxPermSize=512m -XX:+DisableExplicitGC" --link "$MYSQL_CONTAINER":mc-mysql --link "$ES_CONTAINER":mc-es tomcat:"$TOMCAT_VERSION"
fi
echo "Cleaning Tomcat distribution"
docker exec -it mc-tomcat rm -rf /usr/local/tomcat/webapps/ROOT
docker exec -it mc-tomcat rm -rf /usr/local/tomcat/webapps/manager
docker exec -it mc-tomcat rm -rf /usr/local/tomcat/webapps/docs
docker exec -it mc-tomcat rm -rf /usr/local/tomcat/webapps/examples
docker exec -it mc-tomcat rm -rf /usr/local/tomcat/webapps/host-manager

echo "Copying Model Catalogue files"
docker cp conf/docker/mc-config.groovy mc-tomcat:/usr/local/tomcat/conf/mc-config.groovy
docker cp "build/ModelCatalogueCorePluginTestApp-$MC_VERSION.war" mc-tomcat:/usr/local/tomcat/webapps/ROOT.war

echo -e "Tomcat v$TOMCAT_VERSION started at http://$MC_TOMCAT_HOST/."
write_docker_file

