#!/usr/bin/env bash

echo "Travis reported branch $TRAVIS_BRANCH"

#go to home and setup git
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis"

git clone --quiet --branch=2.x https://${GH_TOKEN}@github.com/MetadataRegistry/registry.git registry > /dev/null

cd registry

sed "s/ENV MC_VERSION.*/ENV MC_VERSION $TRAVIS_TAG/" Dockerfile > TemporaryDockerfile
rm Dockerfile
mv TemporaryDockerfile Dockerfile

git add -A .
git commit -m "Travis MetadataRegistry/ModelCataloguePlugin build $TRAVIS_BUILD_NUMBER pushed MetadataRegistry/registry"
git tag "$TRAVIS_TAG"
git push -fq origin 2.x > /dev/null
git push -fq origin "$TRAVIS_TAG" > /dev/null

echo "Metadata Registry notified of successful build"