<%-- 
    Document   : structuretable
    Created on : Apr 6, 2011, 11:31:56 PM
    Author     : Mitch
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<LINK href="css/structurefixer.css" rel="stylesheet" type="text/css">

		<title>Structure Data from Cheshire</title>
		<script type="text/javascript">
			function checkAll()
			{
				var elemSet = document.getElementById('structureDataForm').elements;
				for(var i = 0; i < elemSet.length; i++)
				{
					if( elemSet[i].type.toUpperCase() === "CHECKBOX"
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
					if( elemSet[i].type.toUpperCase() === "CHECKBOX"
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

			function pageToRecordNumber()
			{
				var newRecNum = document.getElementById('structureDataForm').elements["recordNumber"].value;
				var recNum = parseInt(newRecNum);
				if( !isNaN(recNum))
				{
					pageToRec(recNum);
				}
				else
				{
					Alert("Please enter a number.");
				}
			}
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
			}
			.paginationDiv
			{
				font-style:normal;
				color: black;
			}
			
		</style>
	</head>
	<body >
		<form name="structureDataForm" id="structureDataForm"
					action="StructureFixSaver" method="POST">
			<input type="hidden" name="startRec" id="startRec" />

			<div class="formHeader">

				<h2 >Structure Data</h2>
				<br/>
				<button value="Select All" onclick="checkAll();return false" type="button ">Select all</button>
				&nbsp;
				<button value="Select None" onclick="unCheckAll();return false"
								type="button ">Deselect all</button>
				&nbsp;
				<button type="submit">Submit</button>
				&nbsp;
				<button value="Start over" onclick="document.location='index.jsp';return false"
								type="button ">Start over</button>

			</div>
			<div class="queryBox">
					Query:<br/>
				<%=session.getAttribute("FormattedQueryStructure")%>
			</div>
			<%
					int maxRecs = (Integer) session.getAttribute("RecordCount");
					int maxRecordsPerPage = (Integer) session.getAttribute("MaxRecordsPerPage");
					int firstRecord = (Integer) session.getAttribute("FirstRecord");

					String backwardURL = "";
					String forwardURL = "";
			%>
			<br clear="all"/>
			<div class="recNumDiv">
				Displaying record <%=firstRecord%> of <%=maxRecs%>
			</div>
			<br/>
			<!-- Pagination -->
			<%
					if (firstRecord > 1)
					{
						//create 'previous' link
						int prevStartRecord = firstRecord - maxRecordsPerPage;
						if (prevStartRecord < 1)
						{
							prevStartRecord = 1;
						}
						backwardURL = String.format("javascript:pageToRec(%s)", prevStartRecord);
			%>
			<br/>
			<div class="paginationDiv">
				<a href="<%=backwardURL%>">Previous Page</a>
				&nbsp;
				<%
						}
						if ((firstRecord + maxRecordsPerPage) < maxRecs)
						{
							//create 'next' link
							int nextStartRecord = firstRecord + maxRecordsPerPage;
							forwardURL = String.format("javascript:pageToRec(%s)", nextStartRecord);
							//"StructureFixerPager?startRec=" + nextStartRecord;
				%>
				<a href="<%=forwardURL%>">Next Page</a>
				&nbsp;
				<%
						}

						if (maxRecs > maxRecordsPerPage)
						{
				%>
				<a href="javascript:pageToRecordNumber()">Go to record</a>
				<input type="text" name="recordNumber" id="recordNumber"

							 <%								}
							 %>
			</div>
			<br/>
			<div class="tableDiv">
				<%=session.getAttribute("StructureDataTable")%>
			</div>
			<br/>
			<!-- Pagination -->
			<%
					if (backwardURL != null && backwardURL.length() > 0)
					{
			%>
			<a href="<%=backwardURL%>">Previous Page</a>
			&nbsp;
			<%
					}
					if (forwardURL != null && (forwardURL.length() > 0))
					{
			%>
			<a href="<%=forwardURL%>">Next Page</a>
			&nbsp;
			<%
					}

			%>
			<br/>
			<input type="submit" value="Submit"/>
			<button value="Start over" onclick="document.location='index.jsp';return false"
							type="button ">Start over</button>

		</form>
	</body>
</html>
