<!DOCTYPE html>
<html>
<head>
	<title>Genomics England - Rare Diseases - ${disease.name}</title>
	<meta charset="utf-8" />
	<script type="text/javascript" src="inc/jquery.min.js"></script>
	<link rel="stylesheet" type="text/css" href="inc/style.css" />
	<!-- <script src="../file/js/modernizr.js"></script>  -->
</head>
<body>
	<div class="container">
		<h1>${disease.name}</h1>
		<div id="nav">
			<br /><br /><a href="index.html" style="margin-left:20px;">&#171; Back to homepage</a>
		</div>
		<div class="main">
			<ul class="tabs">
				<li>
					<input type="radio" checked name="tabs" id="tab1">
					<label for="tab1">Eligibility</label>
					<div id="tab-content1" class="tab-content animated fadeIn">
            <g:render template="/rareDiseasesWeb/eligibility"/>
					</div>
				</li>
				<li>
					<input type="radio" name="tabs" id="tab2">
					<label for="tab2">Phenotypes</label>
					<div id="tab-content2" class="tab-content animated fadeIn">
            <g:render template="/rareDiseasesWeb/phenotypes"/>
					</div>
				</li>
				<li>
					<input type="radio" name="tabs" id="tab3">
					<label for="tab3">Clinical Tests</label>
					<div id="tab-content3" class="tab-content animated fadeIn">
            <g:render template="/rareDiseasesWeb/clinicalReports"/>
					</div>
				</li>
			</ul>
		</div>
	</div>
</body>
</html>
