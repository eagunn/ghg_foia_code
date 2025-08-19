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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class JvmMemoryAccessor {

    public List<MemoryPool> getPools() throws Exception {

        List<MemoryPool> memoryPools = new LinkedList<MemoryPool>();
        //MBeanServer mBeanServer = new Registry().getMBeanServer();
        //Set memoryOPools = mBeanServer.queryMBeans(new ObjectName("java.lang:type=MemoryPool,*"), null);

        //
        // totals
        //
        long totalInit = 0;
        long totalMax = 0;
        long totalUsed = 0;
        long totalCommitted = 0;

        for (Iterator<MemoryPoolMXBean> it = ManagementFactory.getMemoryPoolMXBeans().iterator(); it.hasNext();) {
        	
            //ObjectInstance oi = (ObjectInstance) it.next();
        	MemoryPoolMXBean item = (MemoryPoolMXBean) it.next();
            //ObjectName oName = oi.getObjectName();
            MemoryPool memoryPool = new MemoryPool();
            //memoryPool.setName(JmxTools.getStringAttr(mBeanServer, oName, "Name"));
            memoryPool.setName(item.getName());
            //memoryPool.setType(JmxTools.getStringAttr(mBeanServer, oName, "Type"));
            MemoryType mt = item.getType();
            memoryPool.setType(mt.name());
            //CompositeDataSupport cd = (CompositeDataSupport) mBeanServer.getAttribute(oName, "Usage");
            //
            // It seems that "Usage" attribute of one of the pools may turn into null intermittently. We better have a
            // deep in the graph then an NPE though.
            //
            MemoryUsage mu = item.getUsage();
            if (mu != null) {
                memoryPool.setMax(mu.getMax());
                memoryPool.setUsed(mu.getUsed());
                memoryPool.setInit(mu.getInit());
                memoryPool.setCommitted(mu.getCommitted());
            } else {
                log.error("Oops, JVM problem? "+item.getName()+" \"Usage\" attribute is NULL!");
            }

            totalInit += memoryPool.getInit();
            totalMax += memoryPool.getMax();
            totalUsed += memoryPool.getUsed();
            totalCommitted += memoryPool.getCommitted();

            memoryPools.add(memoryPool);
        }

        if (!memoryPools.isEmpty()) {
            MemoryPool pool = new MemoryPool();
            pool.setName("Total");
            pool.setType("TOTAL");
            pool.setInit(totalInit);
            pool.setUsed(totalUsed);
            pool.setMax(totalMax);
            pool.setCommitted(totalCommitted);
            memoryPools.add(pool);
        }

        return memoryPools;

    }
}
