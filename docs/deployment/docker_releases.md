# Release Pipeline to GitHub Releases and Docker Image on Git Tag

Each time the tag is pushed into the `2.x` branch the GitHub release is triggered and Docker automated build is run.

There are couple of important things to keep in your mind:

* Don't prefix tag name with `v` as the tag name will become tag for the Docker image and it's not convenient to have the tag prefixed.
