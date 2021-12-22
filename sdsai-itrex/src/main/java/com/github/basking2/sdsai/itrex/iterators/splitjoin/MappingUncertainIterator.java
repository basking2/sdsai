/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.itrex.iterators.splitjoin;

import com.github.basking2.sdsai.itrex.SExprRuntimeException;
import com.github.basking2.sdsai.itrex.iterators.MappingIterator;

import java.util.NoSuchElementException;

/**
 * This does just what {@link com.github.basking2.sdsai.itrex.iterators.MappingIterator} does but for {@link UncertainIterator} objects.
 *
 * The mapping function must define a result.
 */
public class MappingUncertainIterator<T, R> implements UncertainIterator<R> {

    private final UncertainIterator<T> uncertainIterator;
    private final MappingIterator.Mapper<T, R> mapper;

    public MappingUncertainIterator(final UncertainIterator<T> uncertainIterator, final MappingIterator.Mapper<T, R> mapper) {
        this.uncertainIterator = uncertainIterator;
        this.mapper = mapper;
    }

    @Override
    public R next() {
        try {
            return mapper.map(uncertainIterator.next());
        }
        catch (final NoSuchElementException e) {
            throw e;
        }
        catch (final Throwable e) {
            throw new SExprRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public HAS_NEXT hasNext() {
        return uncertainIterator.hasNext();
    }
}
