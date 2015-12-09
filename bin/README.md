Helper shell scripts. These are referred by root `./catalogue` script using functions from `/lib/delegate.sh`.

Nested folders means subcommands e.g. `./catalogue run dev`.

Create text file with `.txt` extension next to your subcommand folder or script and its first line will be included
into the usage help output and the whole file will be printed if you call the subcommand with `--help`.

All scripts are executed in context of the root project folder (i.e. refer to `conf` directory as )