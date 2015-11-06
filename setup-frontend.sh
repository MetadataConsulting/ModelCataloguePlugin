#!/bin/bash

set -e

echo "executing npm install in folders where package.json is exists"

./where package.json run npm install

echo "executing npm install in folders where bower.json is exists"

./where bower.json run bower install