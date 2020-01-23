<%-- 
    Document   : index
    Created on : Mar 27, 2011, 9:19:15 PM
    Author     : Mitch
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"
				import="com.thinkscience.demo.utilities.HttpUtilities"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Structure Cleaner (Marvin JS)</title>
<link type="text/css" rel="stylesheet" href="./scripts/Marvin/css/doc.css" />
	<link type="text/css" rel="stylesheet" href="./scripts/Marvin/js/lib/rainbow/github.css" />
	
<!--script src="./scripts/Marvin/js/lib/rainbow/rainbow-custom.min.js"></script-->
<script src="./scripts/Marvin/js/lib/jquery-1.9.1.min.js"></script>
<!--script src="./scripts/Marvin/js/util.js"></script-->
<script src="https://marvinjs.chemicalize.com/v1/f5bdb6b799f247128abe41ad823f8e87/client-settings.js"></script>
<script src="https://marvinjs.chemicalize.com/v1/client.js"></script>

	<script type="text/javascript">
		var globalMarvin;
			function trace(s)
			{
				if ('console' in self && 'log' in console) console.log(s);
			}
			function getLastStructure()
			{
				<%=HttpUtilities.formatMolfile((String)session.getAttribute("OrientationStructure"))%>
			}
			function copyStructure_prev()
			{
				//var applet = document.getElementById("OrientationStructure");
				var molfile = marvinController.sketcherInstance.exportAsMol();;
				
				var structFormElem = document.getElementById("OrientationStructureAsMolfileString");
				structFormElem.value = molfile;
			}
			
			function copyStructureAndSubmit()
			{
				console.log('in copyStructure');
				globalMarvin.exportStructure('mol').then(function(s){
					var structFormElem = document.getElementById("OrientationStructureAsMolfileString");
					structFormElem.value = s;
					document.forms['queryForm'].submit();
				});
			}


			function loadPreviousSearchData()
			{
				//See if we have a molweight value
				var molWtValue = "400";
				var lastMolWtMax = '${sessionScope["LastMolWtMax"]}';
				trace("lastMolWtMax: " + lastMolWtMax );
				var molWtText = document.getElementById("MolWtMax");
				if( lastMolWtMax != null && lastMolWtMax.length > 0)
				{
					molWtValue= lastMolWtMax;
				}
				molWtText = document.getElementById("MolWtMax");
				molWtText.value= molWtValue;

				//locator code
				var locatorValue = "";
				var lastLocatorValue = "${sessionScope['LastLocatorValue']}";
				trace("lastLocatorValue (from session): " + lastLocatorValue);
				if( lastLocatorValue != null && lastLocatorValue.length > 0)
				{
					locatorValue = lastLocatorValue;
					//remove quotes
					if( locatorValue.charAt(0) == "'")
					{
						locatorValue = locatorValue.substr(1);
					}
					if( locatorValue.charAt(locatorValue.length-1) == "'")
					{
						locatorValue = locatorValue.substr(0,locatorValue.length-1);
					}

				}
				var locatorText = document.getElementById("LocatorCodeValue");
				locatorText.value = locatorValue;

				//Checkbox for clean before orienting
				var cleanBeforeOrient = '${sessionScope["CleanBeforeOrienting"]}';
				trace("cleanBeforeOrient: " + cleanBeforeOrient);
				var cleanBeforeOrientObj = document.getElementById("CleanBeforeOrient");
				if( cleanBeforeOrient != null && cleanBeforeOrient == 'FALSE')
				{
					cleanBeforeOrientObj.checked =false;
					trace("unchecked CleanBeforeOrient" );
				}
						
				//retrieve the structure passed back from the server
				var lastStructure = getLastStructure();//
				trace("lastStructure: " + lastStructure );

				if( lastStructure != null && lastStructure.length > 10)
				{
					console.log('eventually import struct');
					globalMarvin.importStructure('mol', lastStructure).then(function(s){
						console.log('s: ' + s);
					});
					//globalMarvin.sketcherInstance.importAsMol(lastStructure);
				}
			}

			//Clear out all data from the form
			function resetForm()
			{
				//var applet = document.getElementById("OrientationStructure");
				var jsDrawObj = JSDraw.get("structureEditor");
				jsDrawObj.clear();
				//applet.clearMolecule();

				var locatorText = document.getElementById("LocatorCodeValue");
				locatorText.value = "";

				var molWtText = document.getElementById("MolWtMax");
				molWtText.value= "";

				var CitationCountMinText = document.getElementById("CitationCountMin");
				CitationCountMinText.value= "";
			}

			/*$(document).ready(function ()
			{
				//Give the browser a second to load the applet
				setTimeout(function () { loadPreviousSearchData() }, 1000); 
			});*/

		</script>
		<LINK href="css/structurefixer.css" rel="stylesheet" type="text/css">

	</head>
	<body>
		<h2>Structure Fixer Start Page</h2>
		<h4>Version 3.3 (Marvin for JS 18.26)</h4>
		<table>
			<tr>
				<td>
		<h3>Fill in a template/query structure and select the 'Process' button</h3>
		<form name="queryForm" action="StructureFixerServlet" method="POST">
<div id="marvin-test" style="width: 600px; height: 450px"></div>
    <script>
        ChemicalizeMarvinJs.createEditor("#marvin-test").then(function (marvin) {
					console.log('assigning marvin');
					globalMarvin = marvin;
					loadPreviousSearchData();
    });;
    </script>
			<br/>
			<div class="formElementDiv" style="text-align: left;margin-right:auto;width:30%;">
				<br/>
			</td>
			<td style="padding-left: 10px; padding-top: 100px; vertical-align: central">
				<table >
					<tr>
						<td colspan="2">
							Clean structures before applying template?
						</td>
						<td>
							<input type="checkbox" name="CleanBeforeOrient" checked="false" value="ON"
										 id="CleanBeforeOrient">
						</td>
					</tr>
					<tr>
						<td colspan="2">Maximum Molecular Weight </td><td>
							<input type="text" maxlength="8" name="MolWtMax" id="MolWtMax" value="400" size="10"/>
						</td>
					</tr>
					<tr>
						<td colspan="2">Minimum Citation Count</td><td>
							<input type="text" name="CitationCountMin" id="CitationCountMin"
										 maxlength="8" value="${sessionScope['LastCitationCount']}" size="10"/>
						</td>
					</tr>

					<tr>
						<td>Locator Code</td>
						<td><select name="LocatorCodeOperator" id="LocatorCodeOperator">
								<option value="=">Equals</option>
								<option value="starts">Starts with</option>
								<option value="contains">Contains</option>
							</select>
						</td>
						<td>
							<input type="text" maxlength="20" name="LocatorCodeValue" id="LocatorCodeValue"
										 value="" size="20"/>
						</td>
					</tr>

					<tr>
						<td>		<input type="button" value="Process " onclick="copyStructureAndSubmit()" />
				&nbsp;
				<input type="Button" value="Clear" onclick="resetForm();"/>
</td>
					</tr>
					<br/>
					<input type="hidden" name="OrientationStructureAsMolfileString"
								 id="OrientationStructureAsMolfileString" />
				</table>
			</td>
</tr>
</table>
				<br/>

			</div>
		</form>
	</body>
</html>
