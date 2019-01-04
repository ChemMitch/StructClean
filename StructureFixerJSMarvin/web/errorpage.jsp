<%-- 
    Document   : errorpage
    Created on : Apr 4, 2011, 10:01:09 PM
    Author     : Mitch
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
	String ErrorMessage =  "Unknown";
	ErrorMessage = (String)request.getAttribute("ErrorMessage");

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error Page</title>
    </head>
    <body>
        <h1>An error occurred using this application</h1>
				<br>
				<%=ErrorMessage%>
    </body>
</html>
