#!/usr/bin/env bash

set -x

#go to home and setup git
cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis"

git clone --quiet --branch=2.x https://${GH_TOKEN}@github.com/MetadataConsulting/registry.git registry > /dev/null

cd registry

sed "s/ENV MC_VERSION.*/ENV MC_VERSION $TRAVIS_TAG/" Dockerfile > TemporaryDockerfile
rm Dockerfile
mv TemporaryDockerfile Dockerfile

git add -f Dockerfile
git commit -m "Travis MetadataConsulting/ModelCataloguePlugin build $TRAVIS_BUILD_NUMBER pushed MetadataConsulting/registry"
git tag "$TRAVIS_TAG"
git push -fq origin 2.x > /dev/null
git push -fq origin "$TRAVIS_TAG" > /dev/null

curl -H "Content-Type: application/json" --data '{"source_type": "Tag", "source_name": "'"$TRAVIS_TAG"'"}' -X POST "https://registry.hub.docker.com/u/metadata/registry/trigger/$DOCKER_HUB_TRIGGER_TOKEN/"

echo "Metadata Registry notified of successful build"
