<!DOCTYPE html>
<html>
<head>
    <title>Test</title>
    <asset:javascript src="modelcatalogue/app.js"/>
</head>

<body>

<div ng-app="mc.core">
    <decorated-list-table list="muList" columns="[
      {header: 'ID', value: 'id'}
      {header: 'Name', value: 'name'}
    ]"></decorated-list-table>
</div>

<p>test</p>

</body>
</html>