<%-- 
    Document   : processingresult
    Created on : Jun 2, 2011, 8:28:16 PM
    Author     : Mitch
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%@page contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Results of Structure Processing</title>

		<script type="text/javascript">
			function goToStart()
			{
				document.location= "index.jsp";
			}

			function openLogoutWindow()
			{
				window.open("logoutwindow.jsp", "LogoutWindow", 
				"menubar=0,resizable=1,width=350,height=250")
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

		</script>

	</head>
	<body>
		<%
			Boolean processingResult = (Boolean) request.getAttribute("ProcessingResult");
			if (processingResult != null && processingResult)
			{
		%>
		<h1>Structures with the following IDs were processed</h1>
		<ul>
			<c:forEach var="currID"
								 items="${IDsToProcess}">
				<li>${currID}</li>
			</c:forEach>

		</ul>
		<%				} else
		{
		%>
		<h1>Processing failed</h1>
		<%					}
		%>
		<br/>
		<h3>You can</h3>
		<button value="Start over" onclick="resetWork();return false" title="Reset all previous input"
						type="button ">Start over</button>

		<button value="Rerun same query" onclick="rerunQuery();return false"
						title="Return to the query page with same query"
						type="button ">Return to query page</button>
		&nbsp;
		<button type="button" onclick="openLogoutWindow(); return false">Log out</button>

		<form name="resetForm" action="ResetServlet" method="POST">
			<input type="hidden" name="degree" id="degree" value="FULL"/>
		</form>

	</body>
</html>
