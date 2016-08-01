<html>
<head>
  <title>Genomics England - Rare Diseases Reference Data</title>
  <link href="inc/bootstrap-responsive.min.css" rel="stylesheet" type="text/css">
  <link href="inc/united.min.css" rel="stylesheet" type="text/css">
  <style>
  .highlighted { background: yellow; }
  .filter { margin: 1em; padding-left: 30px; background: transparent 0 0 url('inc/search.png') no-repeat; }
  .filter input { width: 250px; }

  table.filterable tbody tr:nth-child(even) {
    background-color: #f0f0f0;
  }
  </style>
</head>
<body>
<h1 style="margin:0.5em;">Genomics England - Rare Disease Reference Data</h1>
<table class="filterable" style="margin:0.5em 1em;">
  <thead>
  <tr style="background-color:#eeeeee;">
    <th style="width:200px;">Model Catalogue ID</th>
    <th style="width:600px;">Disease Name</th>
    <th style="width:200px;">Details</th>
  </tr>
  </thead>
  <tbody>
  <g:each in="${diseases}" var="disease">
    <tr style="border-bottom:1px solid #aaaaaa;">
      <td>${disease.latestVersionId ?: disease.id}</td>
      <td>${disease.name}</td>
      <td><a href="${disease.latestVersionId ?: disease.id}.${disease.versionNumber}.html">details &#187;</a></td>
    </tr>
    </tbody>
  </g:each>
</table>
<p style="margin:1em;"><em>Generated on ${new Date()}</em></p>
<script type="text/javascript" src="inc/filterTable.js"></script>
</body>
</html>
