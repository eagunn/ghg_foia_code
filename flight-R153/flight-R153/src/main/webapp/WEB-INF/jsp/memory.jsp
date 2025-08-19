<%--
  ~ Licensed under the GPL License. You may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~   http://probe.jstripe.com/d/license.shtml
  ~
  ~  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  ~  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  ~  WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib uri="/WEB-INF/tags/jstripe.tld" prefix="inf" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html>
<head>
    <title>JVM memory usage</title>
    <link rel="stylesheet" href="<c:url value="/css/classic/tables.css"/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value="/css/classic/main.css"/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value="/css/classic/mainnav.css"/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value="/css/classic/messages.css"/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value="/css/classic/tooltip.css"/>" type="text/css"/>
    <script type="text/javascript" language="javascript" src="<c:url value="/js/prototype.js"/>"></script>
    <script type="text/javascript" language="javascript" src="<c:url value="/js/scriptaculous.js"/>"></script>
    <script type="text/javascript" language="javascript" src="<c:url value="/js/func.js"/>"></script>
</head>

<c:set var="navTabSystem" value="active" scope="request"/>
<c:set var="systemTabMemory" value="active" scope="request"/>
<c:set var="use_decorator" value="system" scope="request"/>

<body>

<c:choose>
    <c:when test="${empty pools}">
        <div class="errorMessage">
            <p>
                This page requires Java5 with enabled JMX Agent. To enable the JXM Agent please
                add "-Dcom.sun.management.jmxremote" to java command line or $JAVA_OPTS environment
                variable.
            </p>
        </div>
    </c:when>
    <c:otherwise>
        <c:url value="/helpdesk/chart.cht" var="fullChartBase">
            <c:param name="p" value="memory_usage"/>
            <c:param name="xz" value="750"/>
            <c:param name="yz" value="350"/>
        </c:url>

        <div>

			<br/>
            <h3 style="float:none; margin-top:20px; margin-bottom:20px">Current memory usage</h3>

            <div id="memoryPools">
                <div class="ajax_activity"></div>
            </div>

            <h3 style="float:none; margin-top:20px; margin-bottom:20px">Memory usage history</h3>

            <div id="memChartGroup">

                <c:forEach items="${pools}" var="pool" varStatus="status">

                    <c:url value="/helpdesk/chart.cht" var="chartUrl" scope="page">
                        <c:param name="p" value="memory_usage"/>
                        <c:param name="sp" value="${pool.name}"/>
                        <c:param name="xz" value="228"/>
                        <c:param name="yz" value="120"/>
                        <c:param name="l" value="false"/>
                    </c:url>

                    <c:set var="cookie_name" value="mem_${pool.id}" scope="page"/>

                    <c:choose>
                        <c:when test="${cookie[cookie_name].value == 'off'}">
                            <c:set var="style" value="display:none"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="style" value=""/>
                        </c:otherwise>
                    </c:choose>

                    <div class="memoryChart" id="${pool.id}" style="${style}">
                        <dl>
                            <dt><div>
                                ${pool.name}
                                <img onclick="togglePanel('${pool.id}', '<c:url value="/remember.ajax?cn=mem_${pool.id}"/>')"
                                     src="${pageContext.request.contextPath}<spring:theme code="bullet_arrow_down.gif"/>" alt=""/>
                            </div>
                            </dt>
                            <dd class="image"><img id="img_${pool.id}"
                                                   src="<c:out value="${chartUrl}" escapeXml="false"/>" alt=""
                                                   onclick="zoomIn('${pool.name}')"/></dd>
                        </dl>
                    </div>

                    <script type="text/javascript">
                        new Ajax.ImgUpdater("img_${pool.id}", 30);
                    </script>

                </c:forEach>
            </div>

            <div id="fullMemoryChart" style="display: none;">
                <img id="fullImg" class="clickable" src="${fullChartBase}&sp=Total" alt="" onclick="zoomOut();"/>
            </div>
        </div>

        <script type="text/javascript">

            var fullImageUpdater;

            function zoomIn(newPool) {
                if (fullImageUpdater) {
                    fullImageUpdater.stop();
                }
                var img = document.getElementById('fullImg');
                Effect.DropOut('memChartGroup');
                Effect.Appear('fullMemoryChart');
                fullImageUpdater = new Ajax.ImgUpdater("fullImg", 30, '<c:out value="${fullChartBase}" escapeXml="false"/>&sp=' + newPool + "&s1l=" + newPool);
            }

            function zoomOut() {
                Effect.DropOut('fullMemoryChart');
                Effect.Appear('memChartGroup');
                if (fullImageUpdater) {
                    fullImageUpdater.stop();
                    fullImageUpdater=null;
                }
            }

            new Ajax.PeriodicalUpdater("memoryPools", "<c:url value="/helpdesk/memory.ajx"/>?<%=request.getQueryString()%>", {frequency: 5});

        </script>
    </c:otherwise>
</c:choose>

</body>
</html>