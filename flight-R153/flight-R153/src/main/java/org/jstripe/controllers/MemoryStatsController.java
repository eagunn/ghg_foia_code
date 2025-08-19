/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://probe.jstripe.com/d/license.shtml
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.jstripe.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jstripe.model.JvmMemoryAccessor;
import org.jstripe.model.RuntimeInfoAccessor;
import org.jstripe.model.RuntimeInformation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MemoryStatsController {

	@Inject
	protected JvmMemoryAccessor jvmMemoryAccessor;

	@Inject
	protected RuntimeInfoAccessor runtimeInfoAccessor;
	
    public JvmMemoryAccessor getJvmMemoryAccessor() {
        return jvmMemoryAccessor;
    }

    public void setJvmMemoryAccessor(JvmMemoryAccessor jvmMemoryAccessor) {
        this.jvmMemoryAccessor = jvmMemoryAccessor;
    }

    public RuntimeInfoAccessor getRuntimeInfoAccessor() {
        return runtimeInfoAccessor;
    }

    public void setRuntimeInfoAccessor(RuntimeInfoAccessor runtimeInfoAccessor) {
        this.runtimeInfoAccessor = runtimeInfoAccessor;
    }
    
    /*protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView(getViewName(), "pools", jvmMemoryInfoAccessorBean.getPools());
    }*/

    @RequestMapping(value = "/helpdesk/osinfo.ajx", method = RequestMethod.POST)
    protected ModelAndView showOsInfo() throws Exception {
    	Map model = new HashMap();
    	RuntimeInformation ri = runtimeInfoAccessor.getRuntimeInformation();
    	model.put("runtime", ri);    	
    	long uptime = ri.getUptime();
        long uptime_days = uptime / (1000 * 60 * 60 * 24);
        uptime = uptime % (1000 * 60 * 60 * 24);
        long uptime_hours = uptime / (1000 * 60 * 60);
        uptime = uptime % (1000 * 60 * 60);
        long uptime_mins = uptime / (1000 * 60);
        model.put("uptime_days", uptime_days);
        model.put("uptime_hours", uptime_hours);
        model.put("uptime_mins", uptime_mins);
        model.put("garbage_collectors", ri.getGcList());
    	ModelAndView mav = new ModelAndView("ajax/osinfo", model);
    	return mav;
    }
   
    @RequestMapping(value = "/helpdesk/osinfo.do", method = RequestMethod.GET)
    protected ModelAndView showOsCharts() throws Exception {
    	Map model = new HashMap();
    	RuntimeInformation ri = runtimeInfoAccessor.getRuntimeInformation();
    	model.put("runtime", ri);
    	ModelAndView mav = new ModelAndView("osinfo", model);
    	return mav;
    }   
    
    @RequestMapping(value = "/helpdesk/memory.ajx", method = RequestMethod.POST)
    protected ModelAndView showMemoryPools() throws Exception {
    	ModelAndView mav = new ModelAndView("ajax/memory_pools", "pools", jvmMemoryAccessor.getPools());
    	return mav;
    }
   
    @RequestMapping(value = "/helpdesk/memory.do", method = RequestMethod.GET)
    protected ModelAndView showMemoryCharts() throws Exception {
    	Map model = new HashMap();
    	model.put("runtime", runtimeInfoAccessor.getRuntimeInformation());
    	model.put("pools", jvmMemoryAccessor.getPools());
    	ModelAndView mav = new ModelAndView("memory", model);
    	return mav;
    }
    
}
