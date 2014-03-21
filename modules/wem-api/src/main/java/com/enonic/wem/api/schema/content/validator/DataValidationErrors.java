package com.enonic.wem.api.schema.content.validator;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public final class DataValidationErrors
    implements Iterable<DataValidationError>
{
    private final ImmutableList<DataValidationError> validationErrors;

    private DataValidationErrors( final ImmutableList<DataValidationError> validationErrors )
    {
        this.validationErrors = validationErrors;
    }

    public int size()
    {
        return this.validationErrors.size();
    }

    public boolean hasErrors()
    {
        return !this.validationErrors.isEmpty();
    }

    public DataValidationError getFirst()
    {
        return this.validationErrors.isEmpty() ? null : this.validationErrors.iterator().next();
    }

    @Override
    public Iterator<DataValidationError> iterator()
    {
        return this.validationErrors.iterator();
    }

    public int hashCode()
    {
        return this.validationErrors.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof DataValidationErrors ) && this.validationErrors.equals( ( (DataValidationErrors) o ).validationErrors );
    }

    public String toString()
    {
        return this.validationErrors.toString();
    }

    public static DataValidationErrors empty()
    {
        final ImmutableList<DataValidationError> list = ImmutableList.of();
        return new DataValidationErrors( list );
    }

    public static DataValidationErrors from( final DataValidationError... validationErrors )
    {
        return new DataValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }

    public static DataValidationErrors from( final Iterable<DataValidationError> validationErrors )
    {
        return new DataValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }
}
