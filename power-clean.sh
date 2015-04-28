#!/bin/bash

# fail if any line fails
set -e

cd ModelCatalogueCorePluginTestApp

./grailsw clean-all

rm -rf target
rm -rf target-eclipse

./grailsw refresh-dependencies

cd ..
echo "Application cleaned properly"