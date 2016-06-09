# Show logs

Go to `/console` URL of the catalogue and enter following script:

```
File home = new File(System.getenv('CATALINA_HOME'))
File logs = new File(home, 'logs')

logs.eachFile { 
    println()
    println("begin of $it.name".center(60, '>'))
    println it.text
    println("end of $it.name".center(60, '<'))
    println()
}
```

This will print contents of all the logs found on the server.
