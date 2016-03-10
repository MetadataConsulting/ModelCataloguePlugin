# Duplicate entry '1-' for key 'version_number' when importing MC file

If you are getting `MySQLIntegrityConstraintViolationException - Duplicate entry '1-' for key 'version_number'` when importing MC file you are trying to insert different elements with the same model catalogue id. This may happen on the older instances if the model catalogue is empty string instead of `null`.


_Example_
```
dataElement(name: 'one') {
    id ""
}
dataElement(name: 'two') {
    id ""
}
```
