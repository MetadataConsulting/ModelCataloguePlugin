# Shell Scripts

All the scripts which were located directly inside the ModelCataloguePlugin root project folder are being step by step
migrated under the `bin` directory. When finished there should be only one Model Catalogue
specific script - `./catalogue`. In most of use cases you just have to prefix the call with
`./catalogue.`

Examples:

```shell
./catalogue run dev
./catalogue run prod
./catalogue test all
./catalogue clean all
```

Run `./catalogue` to get the list of commands available.

Run `./catalogue <command> --help` to get help for specific command

To run a production server locally the database is configured using the mc-config.groovy file, an example file 
can be found here: 
[Configuration](../deployment/production.adoc)
