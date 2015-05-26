#!/bin/bash

# fail if any line fails
set -e

mkdir -p build

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
./grailsw war

cp target/*.war ../build/



