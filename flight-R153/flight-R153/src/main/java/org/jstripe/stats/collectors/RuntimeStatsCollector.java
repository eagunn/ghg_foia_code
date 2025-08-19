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

import org.jstripe.model.RuntimeInfoAccessor;
import org.jstripe.model.RuntimeInformation;

public class RuntimeStatsCollector extends BaseStatsCollector {
	
    private RuntimeInfoAccessor runtimeInfoAccessor;

    public RuntimeInfoAccessor getRuntimeInfoAccessor() {
        return runtimeInfoAccessor;
    }

    public void setRuntimeInfoAccessor(RuntimeInfoAccessor runtimeInfoAccessor) {
        this.runtimeInfoAccessor = runtimeInfoAccessor;
    }

    public void collect() throws Exception {
        RuntimeInformation ri = runtimeInfoAccessor.getRuntimeInformation();
        if (ri != null) {
            long time = System.currentTimeMillis();
            buildAbsoluteStats("os.memory.committed", ri.getCommittedVirtualMemorySize()/1024, time);
            buildAbsoluteStats("os.memory.physical", (ri.getTotalPhysicalMemorySize() - ri.getFreePhysicalMemorySize())/1024, time);
            buildAbsoluteStats("os.memory.swap", (ri.getTotalSwapSpaceSize() - ri.getFreeSwapSpaceSize())/1024, time);
            //
            // processCpuTime is in nano-seconds, to build timePercentageStats both time parameters have to use
            // in the same units.
            //
            buildTimePercentageStats("os.cpu", ri.getProcessCpuTime() / 1000000, time);
        }
    }
}
