#!/bin/bash

# fail if any line fails
set -e

./setup-frontend.sh

cd ModelCatalogueCorePluginTestApp
./grailsw prod run-app

