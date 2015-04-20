#!/bin/sh

if [ "$TEST_SUITE" = "app_functional" ] ; then
  echo "Starting to update gh-pages\n"

  #copy data we're interested in to other place
  mkdir -p $HOME/reports/last-tests-reports
  cp -Rf ModelCatalogueCorePlugin/target/test-reports $HOME/reports/last-tests-reports
  mkdir -p $HOME/reports/test-app-last-tests-reports
  cp -Rf ModelCatalogueCorePluginTestApp/target/test-reports $HOME/reports/test-app-last-tests-reports
  mkdir -p $HOME/reports/test-app-functional-geb-reports
  cp -Rf ModelCatalogueCorePluginTestApp/target/geb-reports $HOME/reports/test-app-functional-geb-reports

  #go to home and setup git
  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"

  #using token clone gh-pages branch
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/MetadataRegistry/ModelCataloguePluginReports.git gh-pages > /dev/null

  #go into diractory and copy data we're interested in to that directory
  cd gh-pages
  rm -rf ./reports/latest/
  mkdir -p ./reports/latest/
  cp -Rf $HOME/reports/* ./reports/latest/

  #add, commit and push files
  git add -A .
  git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo "Done magic with reports\n"
fi