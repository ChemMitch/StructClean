<%-- 
    Document   : structuretable2
    Created on : Apr 6, 2011, 11:31:56 PM
    Author     : Mitch
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="cache-control" content="no-cache" >

		<LINK href="css/structurefixer.css" rel="stylesheet" type="text/css">
<script src="./scripts/Marvin/js/lib/rainbow/rainbow-custom.min.js"></script>

<script src="./scripts/Marvin/js/lib/jquery-1.9.1.min.js"></script>
<script src="https://marvinjs.chemicalize.com/v1/f5bdb6b799f247128abe41ad823f8e87/client-settings.js"></script>
<script src="https://marvinjs.chemicalize.com/v1/client.js"></script>
	
		
		<title>Structure Data from Cheshire</title>
		
		<div id="marvin-required" style="height: 1px; width: 1px;">
		</div>
		<%=session.getAttribute("displayFunction")%>

		<script type="text/javascript">
			function checkAll()
			{
				var elemSet = document.getElementById('structureDataForm').elements;
				for(var i = 0; i < elemSet.length; i++)
				{
					if( elemSet[i].type.toUpperCase() == "CHECKBOX"
						&& elemSet[i].name.indexOf('includeID_')> -1)
					{
						elemSet[i].checked = true;
					}
				}
			}

			function unCheckAll()
			{
				var elemSet = document.getElementById('structureDataForm').elements;
				for(var i = 0; i < elemSet.length; i++)
				{
					if( elemSet[i].type.toUpperCase() == "CHECKBOX"
						&& elemSet[i].name.indexOf('includeID_')> -1)
					{
						elemSet[i].checked = false;
					}
				}
			}

			function pageToRec(rec)
			{
				document.getElementById('structureDataForm').elements["startRec"].value=rec;
				document.getElementById('structureDataForm').action ="StructureFixerPager";
				document.getElementById('structureDataForm').submit();

			}

			function pageToRecordNumber(recordNumberElementName)
			{
				var newRecNum = document.getElementById('structureDataForm').elements[recordNumberElementName].value;
				var recNum = parseInt(newRecNum);
				if( !isNaN(recNum))
				{
					pageToRec(recNum);
				}
				else
				{
					alert("Please enter a number.");
				}
			}
			
			//submit to a form whose action will reset all input values
			function resetWork()
			{
				document.forms["resetForm"].submit();
			}
			
			//submit to a form whose action but keep query structure and text
			function rerunQuery()
			{
				document.getElementById("degree").value="PARTIAL";
				document.forms["resetForm"].submit();
			}

			$(document).ready(function handleDocumentReady (e) {
				//displayAllStructures();
			});
		</script>

		
		<style type="text/css">

			.queryBox
			{
				float: right;
				width: 20%;

			}

			.formHeader
			{
				float: left;
				width: 80%;
			}

			body
			{
				text-align: center;
			}

			.tableDiv
			{
				clear: both;
				color: black;
				background-color: white;
			}

			.recNumDiv
			{
				font-style: italic;
				color: brown;
				text-align: center;
				clear: both;
			}
			.paginationDiv
			{
				font-style:normal;
				color: black;
				text-align: center;
			}
		</style>
	</head>
	<body >
		<form name="structureDataForm" id="structureDataForm"
					action="StructureFixSaver" method="POST">
			<input type="hidden" name="startRec" id="startRec" />

			<input type="hidden" id="tempPlace"/>
			<div class="formHeader">

				<h2 >Structure Data</h2>
				<br/>
			</div>
			<div id="queryBox1">
				Query:<br/>
				<%=session.getAttribute("FormattedQueryStructure")%>
			</div>
			<br/>
			<!-- c:set var="maxRecs" value="${sessionScope.RecordCount}" scope="page" / -->
			<fmt:parseNumber var="maxRecs" value="${sessionScope.RecordCount}" />
			<c:set var="maxRecordsPerPage" 
						 value="${sessionScope.MaxRecordsPerPage}" scope="page" />

			<!-- c:set var="firstRecord" scope="page" value="${sessionScope.FirstRecord}" / -->
			
			<fmt:parseNumber var="firstRecord" value="${sessionScope.FirstRecord}" />
			<c:set var="lastRecord" scope="page" value="${sessionScope.FirstRecord + sessionScope.MaxRecordsPerPage}"/>
			<c:if test="${lastRecord > maxRecs}" >
				<c:set var="lastRecord" value="${maxRecs}" scope="page" />
			</c:if>
			<c:set var="backwardURL" value="" scope="page" />
			<c:set var="forwardURL" value="" scope="page" />
			<div class="recNumDiv">
				Displaying record <c:out value="${firstRecord}" /> -  <c:out value="${lastRecord}" />
				of <c:out value="${maxRecs}" />
			</div>
			<br/>
			<!-- Pagination links for above the data table-->
			<c:if test="${firstRecord > 1}" >
				<c:set var="prevStartRecord" value="${firstRecord - maxRecordsPerPage}" />
				<c:if test="${prevStartRecord < 1}">
					<c:set var="prevStartRecord" value="${1}" />
				</c:if>
				<c:set var="backwardURL" value="javascript:pageToRec(${prevStartRecord})" />
				<br/>
				<div class="paginationDiv">

					<a href="${backwardURL}">Previous Page</a>
					&nbsp;
				</c:if>

				<c:if test="${(firstRecord + maxRecordsPerPage) < maxRecs}">
					<c:set var="nextStartRecord" value="${firstRecord + maxRecordsPerPage}"></c:set>
					<c:set var="forwardURL" value="javascript:pageToRec(${nextStartRecord})"/>
					<a href="${forwardURL}">Next Page</a>
					&nbsp;
				</c:if>
				&nbsp;
				<c:if test="${maxRecs > maxRecordsPerPage}" >
					<a href="javascript:pageToRecordNumber('recordNumberAbove')">Go to record</a>
					<input type="text" maxlength="6" name="recordNumberAbove" id="recordNumberAbove" size="5"/>
					&nbsp;
					<input type="button" value="Go" onclick="javascript:pageToRecordNumber('recordNumberAbove')"/>

				</c:if>

			</div>
			<br/>
			<c:if test="${sessionScope.RecordCount > 0}" >
				<button value="Select All" onclick="checkAll();return false" type="button ">Select all</button>
				&nbsp;
				<button value="Select None" onclick="unCheckAll();return false"
								type="button ">Deselect all</button>
				&nbsp;
				<button type="submit">Submit</button>
			</c:if>
			&nbsp;
			<button value="Start over" onclick="resetWork();return false" title="Reset all previous input"
							type="button ">Start over</button>

			<button value="Rerun same query" onclick="rerunQuery();return false"
							title="Return to the query page with same query"
							type="button ">Return to query page</button>


			<br clear="all"/>
			<div class="tableDiv">
				<c:out value="${sessionScope.StructureDataTable}" escapeXml="false"/>
			</div>
			<br/>
			<!-- Pagination links for the bottom of the page-->
			<c:if test="${firstRecord > 1}" >
				<c:set var="prevStartRecord" value="${firstRecord - maxRecordsPerPage}" />
				<c:if test="${prevStartRecord < 1}">
					<c:set var="prevStartRecord" value="${1}" />
				</c:if>
				<c:set var="backwardURL" value="javascript:pageToRec(${prevStartRecord})" />
				<br/>
				<div class="paginationDiv">

					<a href="${backwardURL}">Previous Page</a>
					&nbsp;
				</c:if>

				<c:if test="${(firstRecord + maxRecordsPerPage) < maxRecs}">
					<c:set var="nextStartRecord" value="${firstRecord + maxRecordsPerPage}"></c:set>
					<c:set var="forwardURL" value="javascript:pageToRec(${nextStartRecord})"/>
					<a href="${forwardURL}">Next Page</a>
					&nbsp;
				</c:if>
				&nbsp;
				<c:if test="${maxRecs > maxRecordsPerPage}" >
					<a href="javascript:pageToRecordNumber('recordNumberBelow')">Go to record</a>
					<input type="text" maxlength="6" name="recordNumberBelow" id="recordNumberBelow" size="5" />
					<input type="button" value="Go" onclick="javascript:pageToRecordNumber('recordNumberBelow')"/>

				</c:if>
				<br/>
			</div>
			<br/>
			<c:if test="${sessionScope.RecordCount > 0}" >

				<input type="submit" value="Submit"/>
			</c:if>
			<button value="Start over" onclick="resetWork();return false" title="Reset all previous input"
							type="button ">Start over</button>

			<button value="Rerun same query" onclick="rerunQuery();return false"
							title="Return to the query page with same query"
							type="button ">Return to query page</button>
		</form>

		<form name="resetForm" action="ResetServlet" method="POST">
			<input type="hidden" name="degree" id="degree" value="FULL"/>
		</form>
		<script type="text/javascript">
			var globalMarvin;

        ChemicalizeMarvinJs.createEditor("#marvin-required").then(function (marvin) {
					console.log('assigning marvin');
					globalMarvin = marvin;
					displayAllViaMarvin();
    });
		function displayOneStructure(molDataItemId, structureItemId, structureContainerId){
				var settings = {
						'carbonLabelVisible' : false,
						'cpkColoring' : true,
						'implicitHydrogen' : 'TERMINAL_AND_HETERO',
						'width' : parseInt(<%=session.getAttribute("StructureViewWidth")%>, 10),
						'height' : parseInt(<%=session.getAttribute("StructureViewHeight")%>, 10)
				};
				var structureData = document.getElementById(molDataItemId).value;// $(molDataItemId).value;
				
				$("#tempPlace").val(structureData);
				structureData = $("#tempPlace").val();

				var structureDataClean = structureData.replace("\n", "");
				var dataUrl;
				try
				{
					dataUrl= globalMarvin.ImageExporter.molToDataUrl(structureDataClean,"image/png",settings);
				}
				catch(ex)
				{
				}
				
				var structureBox = $(structureItemId);
				structureBox.value =dataUrl;
				$(structureItemId).attr("src", dataUrl);
				$(structureContainerId).css("display", "inherit");
			}
</script>

	</body>
</html>