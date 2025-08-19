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

package org.jstripe.model;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RuntimeInfoAccessor {

    public RuntimeInformation getRuntimeInformation() throws Exception {
    	
        RuntimeInformation ri = new RuntimeInformation();

        try {
        	
        	RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
            ri.setStartTime(rb.getStartTime());
            ri.setUptime(rb.getUptime());
            ri.setName(rb.getName());
            ri.setVmVendor(rb.getVmVendor());
            ri.setVmName(rb.getVmName());
            ri.setVmVersion(rb.getVmVersion());
            
            OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            ri.setOsName(os.getName());
            ri.setOsVersion(os.getVersion());
            ri.setNumberProcessors(os.getAvailableProcessors());
            ri.setTotalPhysicalMemorySize(os.getTotalPhysicalMemorySize());
            ri.setCommittedVirtualMemorySize(os.getCommittedVirtualMemorySize());
            ri.setFreePhysicalMemorySize(os.getFreePhysicalMemorySize());
            ri.setFreeSwapSpaceSize(os.getFreeSwapSpaceSize());
            ri.setTotalSwapSpaceSize(os.getTotalSwapSpaceSize());
            ri.setProcessCpuTime(os.getProcessCpuTime());

            ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
            ri.setLoadedClassCount(cl.getTotalLoadedClassCount());
            ri.setUnloadedClassCount(cl.getUnloadedClassCount());
            
            List<GarbageCollectorMXBean> gcbList = ManagementFactory.getGarbageCollectorMXBeans();
            List<GarbageCollector> gcList = new ArrayList<GarbageCollector>();
            for (GarbageCollectorMXBean gcb : gcbList) {
            	GarbageCollector gc = new GarbageCollector();
            	gc.setName(gcb.getName());
            	gc.setCollectionCount(gcb.getCollectionCount());
            	long ct = gcb.getCollectionTime();
            	long ct_hours = ct / (1000 * 60 * 60);
            	ct = ct % (1000 * 60 * 60);
            	long ct_mins = ct / (1000  * 60);
            	ct = ct % (1000 * 60);
            	long ct_sec = ct / 1000;
            	long ct_ms = ct % (1000);
            	gc.setCollectionTime(ct_hours+" hours "+ct_mins+" mins "+ct_sec+" secs "+ct_ms+" ms");
            	gcList.add(gc);
            }
            ri.setGcList(gcList);
            
            return ri;
            
        } catch (Exception e) {
            log.debug("OS information is unavailable");
            return null;
        }
    }
}
