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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.jstripe.com/tags" prefix="inf" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<div class="shadow" style="clear: none;">
    <div>
        <p>
        	Up Time:
        	<span class="uptime">${uptime_days} days ${uptime_hours} hours ${uptime_mins} minutes</span><br/>
        	JVM:
        	<span class="value">${runtime.vmVendor} - ${runtime.vmName} ${runtime.vmVersion}</span><br/>
            <spring:message code="probe.jsp.os.card.name"/>
            <span class="value" title="${runtime.name}">${runtime.osName}</span>
            <spring:message code="probe.jsp.os.card.version"/>
            <span class="value">${runtime.osVersion}</span>
            Processors:
            <span class="value">${runtime.numberProcessors}</span><br/>
            <spring:message code="probe.jsp.os.card.totalMemory"/>
            <span class="value"><inf:volume value="${runtime.totalPhysicalMemorySize}" fractions="2"/></span>
            <spring:message code="probe.jsp.os.card.freeMemory"/>
            <span class="value"><inf:volume value="${runtime.freePhysicalMemorySize}" fractions="2"/></span>
            <spring:message code="probe.jsp.os.card.committedVirtualMemory"/>
            <span class="value"><inf:volume value="${runtime.committedVirtualMemorySize}" fractions="2"/></span><br/>
            <spring:message code="probe.jsp.os.card.totalSwap"/>
            <span class="value"><inf:volume value="${runtime.totalSwapSpaceSize}" fractions="2"/></span>
            <spring:message code="probe.jsp.os.card.freeSwap"/>
            <span class="value"><inf:volume value="${runtime.freeSwapSpaceSize}" fractions="2"/></span><br/>
        	Loaded classes:
        	<span class="value">${runtime.loadedClassCount}</span>
        	Unloaded classes:
        	<span class="value">${runtime.unloadedClassCount}</span>
        	<br/>
			<display:table name="garbage_collectors" id="gc" class="genericTbl" cellspacing="0">
				<display:column property="name" title="Garbage Collector"/>
				<display:column property="collectionCount" title="Collection Count"/>
				<display:column property="collectionTime" title="Collection Time"/>
			</display:table>
        </p>
    </div>
</div>
