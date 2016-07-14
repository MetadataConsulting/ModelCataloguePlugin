# Running on Amazon Web Services

As Model Catalogue is shipped as Docker image it can be easily run using
[Amazon Elastic Container Service](https://eu-west-1.console.aws.amazon.com/ecs/home?region=eu-west-1).


## Setup

### Cluster

First of all you need _Cluster_ available with enough registered
container instances. There is _Cluster_ called _default_ always available
but you need to start new Amazon ESC container instance for it if not
set up yet. Follow the guide here: [Amazon ECS Container Instances](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_instances.html).
The instance should be `m3.xlarge` to allow running Model Catalogue with
enough memory. If you've setup the instance properly it should appear
at _Clusters_ / _ESC Instances_ table.

### Task Definitions
Task definition 
