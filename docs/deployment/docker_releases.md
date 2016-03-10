# Release Pipeline to GitHub Releases and Docker Image on Git Tag

Each time the tag is pushed into the `2.x` branch the GitHub release is triggered and Docker automated build is run.

There are couple of important things to keep in your mind:

* The release is currently triggered within `app_integration` test suite on Travis CI so if some of the other test suits fails the release process will still be triggered. Please, first wait until the normal push to branch tests are finished on Travis CI and than create a tag and push it again to the GitHub repository.
* Don't prefix tag name with `v` as the tag name will become tag for the Docker image and it's not convenient to have the tag prefixed.
