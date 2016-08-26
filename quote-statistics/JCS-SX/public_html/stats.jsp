<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="oracle.cloud.sampleapps.QuoteStatistics.CpqCaller"%>
<%@page import="oracle.cloud.sampleapps.QuoteStatistics.CpqCaller.AvgNetPriceStat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
   CpqCaller caller = new CpqCaller();
   AvgNetPriceStat stat = caller.compileStats(request, response);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Stats</title>
</head>
<body>
	<table>
		<tr>
			<td><%=stat.getMsg() %></td>
			<td><%= stat.hasData()?stat.getAvg():"" %></td>
		</tr>
	</table>
</body>
</html>