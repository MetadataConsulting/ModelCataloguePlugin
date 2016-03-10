# Running interactive code with Grails Console in Production

The [Grails Console Plugin](https://grails.org/plugin/console) is enabled in development environment by default. You can configure running with console plugin in production in you `mc-config.groovy` file by adding following line:

```
grails.plugin.console.enabled=true
```

The console is available at `<mc–url>/console` and by default is restricted to `ADMIN` role only.

The drawback of console is that it doesn't provide updates during the execution - the result printed using `println` or `out << 'text'` is shown after the whole task is finished. The simples way how to get feedback is to use the `log.error 'text'` call which will be appended to system log. I haven't found yet the proper setting to enable logging for console in lower log levels but `error` level is printed every time.
