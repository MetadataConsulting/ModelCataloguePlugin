# nvm already installed

If you have `nvm already installed` alert in the console after running `setup-frontend.sh` script it's probably caused by bug in NVM
which tries use the version listed in `.nvmrc` file immediatelly. To fix this change your directory to somewhere where no `.nvmrc`
file exists and execute.

```
 . ~/.nvm/nvm.sh
 nvm install v5
```
