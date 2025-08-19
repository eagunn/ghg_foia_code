<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <%@page import="java.util.Date" %>
    <%@page import="java.io.File" %>
    <%@page import="java.text.SimpleDateFormat" %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Log Viewer</title>
        <script type="text/javascript" language="JavaScript" src="/ghg/js/jquery.js"></script>
        <script type="text/javascript" language="JavaScript" src="/ghg/js/jquery.tablesorter.js"></script>
        <script type="text/javascript" language="JavaScript">
            $(document).ready(
            function() {
                $(".sortable").tablesorter( {sortList: [ [0,1] ] } );
            } );
        </script>
        <style type="text/css" media="screen">
            table.sortable { font-family:arial; background-color: #CDCDCD; margin:10px 0pt 15px; font-size: 10pt; width: 100%; text-align: left; }
            table.sortable thead tr th, table.sortable tfoot tr th { background-color: #e6EEEE; border: 1px solid #FFF; font-size: 10pt; padding: 4px; }
            table.sortable thead tr .header { background-image: url(/ghg/img/bg.gif); background-repeat: no-repeat; background-position: center right; cursor: pointer; }
            table.sortable tbody td { color: #3D3D3D; padding: 4px; background-color: #FFF; vertical-align: top; }
            table.sortable tbody tr.odd td { background-color:#F0F0F6; }
            table.sortable thead tr .headerSortUp { background-image: url(/ghg/img/asc.gif); }
            table.sortable thead tr .headerSortDown { background-image: url(/ghg/img/desc.gif); }
            table.sortable thead tr .headerSortDown, table.sortable thead tr .headerSortUp { background-color: #8dbdd8; }
        </style>
    </head>
    <body>
        <%
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm aaa");
            String path = System.getProperty("ghgp.root");
            File folder = new File(path + "/logs");
        %>
        <table class="sortable">
            <thead>
                <tr>
                    <th>Last Modified</th>
                    <th>Size [byte]</th>
                    <th>File Name</th>
                </tr>
            </thead>
            <tbody>
                <% for (File file : folder.listFiles()) { if (file.isFile()) { %>
                <tr>
                    <td><%= formatter.format(new Date(file.lastModified())) %></td>
                    <td><%= file.length()%></td>
                    <td><a href="<%= "/ghg/logs/" + file.getName() %>"><%= file.getName() %></a></td>
                </tr>
                <%} }%>
            </tbody>
        </table>
    </body>
</html>
