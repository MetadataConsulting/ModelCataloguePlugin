# Release Pipeline to GitHub Releases and Docker Image on Git Tag

Each time a tag is pushed into the `2.x` branch, the GitHub release process is triggered and Docker automated build is run.

There are couple of important things to keep in mind:

* Don't prefix the tag name with `v`, as the tag name will become a tag for the Docker image, for which it is not convenient to have the tag prefixed.
