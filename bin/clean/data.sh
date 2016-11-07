#!/usr/bin/env bash

cd ModelCatalogueCorePluginTestApp

APP_VERSION=`cat "application.properties" | grep "app.version" | cut -d'=' -f2`

rm -rf "$TMPDIR/ModelCatalogueCorePluginTestApp/$APP_VERSION"
echo "Deleted local data cache ${TMPDIR}ModelCatalogueCorePluginTestApp/$APP_VERSION"

cd ..




