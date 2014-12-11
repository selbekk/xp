package com.enonic.wem.api.query.aggregation;

public abstract class AbstractHistogramAggregationQuery<T extends Interval>
    extends AggregationQuery
{
    private final String fieldName;

    private final Long minDocCount;

    private final T interval;

    AbstractHistogramAggregationQuery( final Builder builder, final T interval )
    {
        super( builder.name );
        this.fieldName = builder.fieldName;
        this.interval = interval;
        this.minDocCount = builder.minDocCount;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public T getInterval()
    {
        return interval;
    }

    public Long getMinDocCount()
    {
        return minDocCount;
    }

    public abstract static class Builder<B extends Builder, T extends Interval>
        extends AggregationQuery.Builder
    {
        private String fieldName;

        T interval;

        private long minDocCount = 1;

        public Builder( final String name )
        {
            super( name );
        }

        @SuppressWarnings("unchecked")
        public B interval( final T interval )
        {
            this.interval = interval;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B fieldName( final String fieldName )
        {
            this.fieldName = fieldName;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B minDocCount( final Long minDocCount )
        {
            this.minDocCount = minDocCount;
            return (B) this;
        }
    }


}