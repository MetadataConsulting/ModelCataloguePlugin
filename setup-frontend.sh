#!/bin/bash

set -e

: ${NPM_REGISTRY_URL:="http://registry.npmjs.org"}

if [ ! -f ~/.nvm/nvm.sh ]; then
    echo "installing nvm"
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.0/install.sh | bash
else
    echo "nvm is already installed on the system"
fi
. ~/.nvm/nvm.sh
nvm install
nvm use

echo "executing npm install in folders where package.json is exists"

./where package.json run npm install --registry "$NPM_REGISTRY_URL"

echo "executing npm install in folders where bower.json is exists"

./where bower.json run bower install

# there is a failing java file which grails tries to compile
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/demo
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/src
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/src-min
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/src-noconflict
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/textarea
rm -f  ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/*.html
rm -f  ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/gulpfile.babel.js
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/src
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/docs
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/sly-repeat/scripts
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/sly-repeat/src
