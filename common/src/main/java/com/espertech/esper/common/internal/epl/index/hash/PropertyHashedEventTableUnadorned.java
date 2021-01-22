/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.epl.index.hash;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.internal.epl.expression.core.ExprEvaluatorContext;

import java.util.*;

public class PropertyHashedEventTableUnadorned extends PropertyHashedEventTable {
    protected final Map<Object, Set<EventBean>> propertyIndex;

    public PropertyHashedEventTableUnadorned(PropertyHashedEventTableFactory factory) {
        super(factory);
        propertyIndex = new HashMap<>();
    }

    /**
     * Returns the set of events that have the same property value as the given event.
     *
     * @param key to compare against
     * @return set of events with property value, or null if none found (never returns zero-sized set)
     */
    public Set<EventBean> lookup(Object key) {
        return propertyIndex.get(key);
    }

    /**
     * Same as lookup except always returns a copy of the set
     * @param key key
     * @return copy
     */
    public Set<EventBean> lookupFAF(Object key) {
        Set<EventBean> result = propertyIndex.get(key);
        return result == null ? null : new LinkedHashSet<>(result);
    }

    public void add(EventBean theEvent, ExprEvaluatorContext exprEvaluatorContext) {
        Object key = getKey(theEvent);
        Set<EventBean> events = propertyIndex.computeIfAbsent(key, k -> new LinkedHashSet<>());
        events.add(theEvent);
    }

    public void remove(EventBean theEvent, ExprEvaluatorContext exprEvaluatorContext) {
        Object key = getKey(theEvent);

        Set<EventBean> events = propertyIndex.get(key);
        if (events == null) {
            return;
        }

        if (!events.remove(theEvent)) {
            // Not an error, its possible that an old-data event is artificial (such as for statistics) and
            // thus did not correspond to a new-data event raised earlier.
            return;
        }

        if (events.isEmpty()) {
            propertyIndex.remove(key);
        }
    }

    public boolean isEmpty() {
        return propertyIndex.isEmpty();
    }

    public Iterator<EventBean> iterator() {
        return new PropertyHashedEventTableIterator<>(propertyIndex);
    }

    public void clear() {
        propertyIndex.clear();
    }

    public void destroy() {
        clear();
    }

    public Integer getNumberOfEvents() {
        return null;
    }

    public int getNumKeys() {
        return propertyIndex.size();
    }

    public Object getIndex() {
        return propertyIndex;
    }

    public Class<?> getProviderClass() {
        return PropertyHashedEventTable.class;
    }
}
