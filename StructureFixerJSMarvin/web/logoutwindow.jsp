<%-- 
    Document   : logoutwindow
    Created on : Jun 7, 2011, 3:03:05 PM
    Author     : Mitch
--%>

<%@page import="java.sql.Connection,java.sql.SQLException"%>
<%@page  import="com.thinkscience.demo.utilities.HttpUtilities" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
	"http://www.w3.org/TR/html4/loose.dtd">

<%
	Connection conn = (Connection) session.getAttribute("OracleConnection");
	HttpUtilities httpUtil = new HttpUtilities();
	try
	{
		if (conn != null && !conn.isClosed())
		{
			conn.close();
			conn = null;
			session.removeAttribute("OracleConnection");
		}
	} catch (SQLException ex)
	{
		httpUtil.processError("Error checking Oracle connection: "
						+ ex.getMessage(),
						request, response, getServletContext());
	}

	HttpUtilities.clearSessionAttributes(session);

%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Logout confirmation</title>
		<script type="text/javascript">
			function returnToIndex()
			{
				window.opener.location ="/index.jsp";
			}
		</script>
	</head>
	<body style="text-align: center">
		<h1>You have closed the database connection.</h1>
		<a href="Javascript:returnToIndex()">Start over</a>
		&nbsp;
		<a href="javascript:window.close();">Close window</a>
	</body>
</html>
