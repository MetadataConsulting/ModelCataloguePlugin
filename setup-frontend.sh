#!/bin/bash

set -e


if [ "$CIRCLECI" = "" ] ; then
    if [ ! -f ~/.nvm/nvm.sh ]; then
        echo "installing nvm"
        curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.31.0/install.sh | bash
    else
        echo "nvm is already installed on the system"
    fi
    set +e
    . ~/.nvm/nvm.sh
    set -e
    nvm install
    nvm use
fi

echo "executing npm install in folders where package.json is exists"

./where package.json run npm install
./where package.json run npm prune

echo "executing npm install in folders where bower.json is exists"

./where bower.json run node_modules/bower/bin/bower install

# there is a failing java file which grails tries to compile
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/demo
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/src
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/src-min
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/src-noconflict
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/textarea
rm -f  ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/ace-builds/*.html
rm -f  ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/angular-file-saver/gulpfile.babel.js
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/angular-file-saver/src
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/angular-file-saver/docs
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/sly-repeat/scripts
rm -rf ModelCatalogueCorePluginTestApp/grails-app/assets/bower_components/sly-repeat/src
