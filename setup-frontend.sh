#!/bin/bash

set -e

if [ ! -f ~/.nvm/nvm.sh ]; then
    echo "installing nvm"
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.0/install.sh | bash
else
    echo "nvm is already installed on the system"
fi
. ~/.nvm/nvm.sh
nvm use

echo "executing npm install in folders where package.json is exists"

./where package.json run npm install

echo "executing npm install in folders where bower.json is exists"

./where bower.json run bower install

# there is a failing java file which grails tries to compile
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/ace-builds/demo
rm -f  ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/gulpfile.babel.js
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/src
rm -rf ModelCatalogueCorePlugin/grails-app/assets/bower_components/angular-file-saver/docs
