/**
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://probe.jstripe.com/d/license.shtml
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.jstripe.stats.providers;

import java.util.List;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public abstract class AbstractSeriesProvider implements SeriesProvider {

    protected XYSeries toSeries(String legend, List stats) {
        XYSeries xySeries = new XYSeries(legend, true, false);
        for (int i = 0; i < stats.size(); i++) {
            xySeries.add((XYDataItem) stats.get(i));
        }
        return xySeries;
    }

}
