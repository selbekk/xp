package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repository.RepositoryId;

public class RelationshipStorageName
    implements StorageName
{
    private final static String RELATIONSHIP_INDEX_PREFIX = "relationships";

    private final static String DIVIDER = "-";

    private final String name;

    private RelationshipStorageName( final String name )
    {
        this.name = name;
    }

    public static RelationshipStorageName from( final RepositoryId repositoryId )
    {
        return new RelationshipStorageName( RELATIONSHIP_INDEX_PREFIX + DIVIDER + repositoryId.toString() );
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final RelationshipStorageName that = (RelationshipStorageName) o;

        return !( name != null ? !name.equals( that.name ) : that.name != null );

    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
