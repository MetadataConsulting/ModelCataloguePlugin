#!/bin/sh

if [ "$TEST_SUITE" = "app_functional" ] || [ "$TEST_SUITE" = "app_functional_a" ]|| [ "$TEST_SUITE" = "app_functional_b" ]|| [ "$TEST_SUITE" = "app_functional_c" ] ; then
  echo "Starting to update gh-pages\n"

  #copy data we're interested in to other place
  mkdir -p $HOME/reports/last-tests-reports
  cp -Rf ModelCatalogueCorePlugin/target/test-reports "$HOME/$TEST_SUITE/reports/last-tests-reports"
  mkdir -p $HOME/reports/test-app-last-tests-reports
  cp -Rf ModelCatalogueCorePluginTestApp/target/test-reports "$HOME/$TEST_SUITE/reports/test-app-last-tests-reports"
  mkdir -p $HOME/reports/test-app-functional-geb-reports
  cp -Rf ModelCatalogueCorePluginTestApp/target/geb-reports "$HOME/$TEST_SUITE/reports/test-app-functional-geb-reports"

  #go to home and setup git
  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"

  #using token clone gh-pages branch
  git clone --quiet --depth=1 --branch=gh-pages https://${GH_TOKEN}@github.com/MetadataRegistry/ModelCataloguePluginReports.git gh-pages > /dev/null

  #go into directory and copy data we're interested in to that directory
  cd gh-pages

  rm -rf ./$TEST_SUITE/reports/
  mkdir -p ./$TEST_SUITE/reports/
  cp -Rf "$HOME/$TEST_SUITE/reports/* ./$TEST_SUITE/reports/"

  #add, commit and push files
  git add -A .
  git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo "Done magic with reports\n"
fi
