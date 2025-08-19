<%@ page import="java.lang.management.*" %>
<%@ page import="java.util.*" %>
<html>
<head>
  <title>JVM Memory Monitor</title>
</head>

<%
        Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
        while (iter.hasNext()) {
            MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
%>

<table border="0" width="100%">
<tr><td colspan="2" align="center"><h3>Memory MXBean</h3></td></tr>
<tr><td
width="200">Heap Memory Usage</td><td><%=
ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()
%></td></tr>
<tr><td>Non-Heap Memory
Usage</td><td><%=
ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
%></td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2" align="center"><h3>Memory Pool MXBeans</h3></td></tr>
<%
        Iterator iter1 = ManagementFactory.getMemoryPoolMXBeans().iterator();
        while (iter1.hasNext()) {
            MemoryPoolMXBean item1 = (MemoryPoolMXBean) iter1.next();
%>
<tr><td colspan="2">
<table border="0" width="100%" style="border: 1px #98AAB1 solid;">
<tr><td colspan="2" align="center"><b><%= item1.getName() %></b></td></tr>
<tr><td width="200">Type</td><td><%= item1.getType() %></td></tr>
<tr><td>Usage</td><td><%= item1.getUsage() %></td></tr>
<tr><td>Peak Usage</td><td><%= item1.getPeakUsage() %></td></tr>
<tr><td>Collection Usage</td><td><%= item1.getCollectionUsage() %></td></tr>
</table>
</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%
}
%>
<%
}
%>
</table>