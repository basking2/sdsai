/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.functions;

import com.github.basking2.sdsai.itrex.EvaluationContext;
import com.github.basking2.sdsai.itrex.iterators.ZipIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;

public class ZipFunction implements FunctionInterface<ZipIterator<Object, Object>> {

    public final static Logger LOG = LoggerFactory.getLogger(ZipFunction.class);

    @Override
    public ZipIterator<Object, Object> apply(final Iterator<?> iterator, final EvaluationContext evaluationContext) {
        boolean pad1 = false;
        boolean pad2 = false;
        Object value1 = null;
        Object value2 = null;
        Iterator<Object> iterator1 = null;
        Iterator<Object> iterator2 = null;

        while (iterator.hasNext()) {
            final Object o = iterator.next();
            final Iterator<Object> i = toIterator(o);
            if (i != null) {
                if (iterator1 == null) {
                    iterator1 = i;
                }
                else if (iterator2 == null) {
                    iterator2 = i;
                }
                else {
                    LOG.warn("Dropped unused argument {}.", i);
                }
            }
            else if (o.toString().equalsIgnoreCase("padLeft") && iterator.hasNext()) {
                pad1 = true;
                value1 = iterator.next();
            }
            else if (o.toString().equalsIgnoreCase("padRight") && iterator.hasNext()) {
                pad2 = true;
                value2 = iterator.next();
            }
            else {
                LOG.warn("Unhandled argument {}.", o);
            }
        }

        return new ZipIterator<>(iterator1, pad1, value1, iterator2, pad2, value2);
    }
}
