<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.openceres.model.ActorInfo" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.openceres.monitor.*" %>

<%!
	SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String startedDate = simpleDate.format(System.currentTimeMillis());
%>

<%
	String refreshTime = request.getParameter("refreshTime");
	refreshTime = (refreshTime == null) ? "10" : refreshTime;
	
	
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html"; charset=UTF-8">
<meta HTTP-equiv="REFRESH" content="<%=refreshTime %> "/>
<link rel="stylesheet" type="text/css" href="default.css">
<h1>Auto Scale monitoring System.</h1>
</head>
<body>
	<div id="menu">
		<table>
			<tr>
				<td><a href="./index.jsp">Actors</a></td>
			</tr> 
		</table>
	</div>
	
	<div id="dfstable">
		<h3> Actor Monitoring</h3>
	<table>
		<tr>
		<td>URI</td>
		<td>Role</td>
		<td>Status</td>
		<td>Last modified time</td>
		<td>Command</td>
		<td>Description</td>
	</tr>
		
	<%
		OpenCeresMonitor openCeresMonitor = new OpenCeresMonitorImpl();
		List<ActorInfo> actorInfos = openCeresMonitor.getActorInfos();
		for(ActorInfo actor : actorInfos)
		{
	%>
		<tr>
			<td><%= actor.getUri() %></td>
			<td><%= actor.getRole() %></td>
			<td><%= actor.getStatus() %></td>
			<td><%= simpleDate.format(actor.getStart()) %></td>
			<td><%= actor.getCommand() %></td>
			<td><%= actor.getDescription() %></td>
		</tr>
	<%
		}
	%>		
	</table>
	</div>
</body>
</html>