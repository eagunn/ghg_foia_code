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

package org.jstripe.stats.collectors;

import java.util.Iterator;
import java.util.List;

import org.jstripe.model.JvmMemoryAccessor;
import org.jstripe.model.MemoryPool;

public class JvmMemoryStatsCollector extends BaseStatsCollector {
	
    private JvmMemoryAccessor jvmMemoryAccessor;

    public JvmMemoryAccessor getJvmMemoryAccessor() {
        return jvmMemoryAccessor;
    }

    public void setJvmMemoryAccessor(JvmMemoryAccessor jvmMemoryAccessor) {
        this.jvmMemoryAccessor = jvmMemoryAccessor;
    }
    
    public void collect() throws Exception {
        List<MemoryPool> pools = jvmMemoryAccessor.getPools();
        long time = System.currentTimeMillis();
        for (Iterator<MemoryPool> it = pools.iterator(); it.hasNext(); ) {
            MemoryPool pool = (MemoryPool) it.next();
            buildAbsoluteStats("memory.pool."+pool.getName(), pool.getUsed(), time);
        }
    }
}
