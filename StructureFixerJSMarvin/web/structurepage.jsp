<%-- 
    Document   : structurepage
    Created on : Apr 3, 2011, 12:04:18 AM
    Author     : Mitch
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/WEB-INF/datagrid.tld" prefix="grd" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@page import="com.thinkscience.demo.structurefixer.objects.OverlaidStructureData"%>

<%
int     intCurr    = 1;
int     intSortOrd = 0;
String  strTmp     = null;
String  strSortCol = null;
String  strSortOrd = "ASC";
List<OverlaidStructureData>    lstData    = null;
boolean blnSortAsc = true;

strTmp = request.getParameter("txtCurr");
try
{
	if (strTmp != null)
		intCurr = Integer.parseInt(strTmp);
}
catch (NumberFormatException NFEx)
{
}
if (request.getAttribute("OverlayData") != null)
	lstData = (List<OverlaidStructureData>) request.getAttribute("OverlayData");
else if( session.getAttribute("OverlayData") != null)
	lstData = (List<OverlaidStructureData>) session.getAttribute("OverlayData");

strSortCol = request.getParameter("txtSortCol");
strSortOrd = request.getParameter("txtSortOrd");
if (strSortCol == null) strSortCol = "id";
if (strSortOrd == null) strSortOrd = "ASC";
blnSortAsc = (strSortOrd.equals("ASC"));
%>
<html>
<head>
<title>Structure Data in a Grid</title>
<link REL="StyleSheet" HREF="css/GridStyle.css">
<script type="text/javascript">
function doNavigate(pstrWhere, pintTot)
{
	var strTmp;
	var intPg;

	strTmp = document.frmMain.txtCurr.value;
	intPg = parseInt(strTmp);
	if (isNaN(intPg)) intPg = 1;

	if ((pstrWhere == 'F' || pstrWhere == 'P') && intPg == 1)
	{
		alert("You are already viewing first page!");
		return;
	}
	else if ((pstrWhere == 'N' || pstrWhere == 'L') && intPg == pintTot)
	{
		alert("You are already viewing last page!");
		return;
	}

	if (pstrWhere == 'F')
		intPg = 1;
	else if (pstrWhere == 'P')
		intPg = intPg - 1;
	else if (pstrWhere == 'N')
		intPg = intPg + 1;
	else if (pstrWhere == 'L')
		intPg = pintTot;

	if (intPg < 1)
		intPg = 1;
	if (intPg > pintTot)
		intPg = pintTot;
	document.frmMain.txtCurr.value = intPg;
	document.frmMain.submit();
}

function doSort(pstrFld, pstrOrd)
{
	document.frmMain.txtSortCol.value = pstrFld;
	document.frmMain.txtSortOrd.value = pstrOrd;
	document.frmMain.submit();
}
</script>
    </head>
    <body>
<h2>Structure Data</h2>
<form NAME="frmMain" METHOD="post">
<grd:dbgrid id="tblStat" name="tblStat" width="100" pageSize="10" currentPage="<%=intCurr%>"
	border="0" cellSpacing="1" cellPadding="2" dataMember="" dataSource="<%=lstData%>" cssClass="gridTable">
	<grd:gridpager imgFirst="images/First.gif" imgPrevious="images/Previous.gif"
		imgNext="images/Next.gif" imgLast="images/Last.gif"></grd:gridpager>
	<grd:gridsorter sortColumn="<%=strSortCol%>" sortAscending="<%=blnSortAsc%>"></grd:gridsorter>
	<grd:rownumcolumn headerText="#" width="5" HAlign="right"></grd:rownumcolumn>
	<grd:textcolumn dataField="id" headerText="ID" width="10" sortable="true"></grd:textcolumn>
	<grd:textcolumn dataField="molName" headerText="Name" width="50" sortable="true"></grd:textcolumn>
	<grd:numbercolumn dataField="molWt" headerText="Mol Wt" width="40" sortable="true"></grd:numbercolumn>
	<grd:textcolumn dataField="molFMLA" headerText="Mol Fmla"  width="50" sortable="false"/>
</grd:dbgrid>
<input TYPE="hidden" NAME="txtCurr" VALUE="<%=intCurr%>">
<input TYPE="hidden" NAME="txtSortCol" VALUE="<%=strSortCol%>">
<input TYPE="hidden" NAME="txtSortOrd" VALUE="<%=strSortOrd%>">
</form>
</body>
</html>