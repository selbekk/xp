package com.enonic.xp.core.impl.schema.content;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

final class ContentTypeRegistry
{
    private final BuiltinContentTypes builtInTypes;

    protected ApplicationService applicationService;

    protected ResourceService resourceService;

    public ContentTypeRegistry()
    {
        this.builtInTypes = new BuiltinContentTypes();
    }

    private boolean isSystem( final ContentTypeName name )
    {
        return SchemaHelper.isSystem( name.getApplicationKey() );
    }

    public ContentType get( final ContentTypeName name )
    {
        if ( isSystem( name ) )
        {
            return this.builtInTypes.getAll().getContentType( name );
        }

        return new ContentTypeLoader( this.resourceService ).get( name );
    }

    public ContentTypes getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getByApplication( key );
        }

        final List<ContentType> list = Lists.newArrayList();
        for ( final ContentTypeName name : findNames( key ) )
        {
            final ContentType type = get( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return ContentTypes.from( list );
    }

    private Set<ContentTypeName> findNames( final ApplicationKey key )
    {
        return new ContentTypeLoader( this.resourceService ).findNames( key );
    }

    public ContentTypes getAll()
    {
        final Set<ContentType> contentTypeList = Sets.newLinkedHashSet();
        contentTypeList.addAll( this.builtInTypes.getAll().getList() );

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            final ContentTypes contentTypes = getByApplication( application.getKey() );
            contentTypeList.addAll( contentTypes.getList() );
        }

        return ContentTypes.from( contentTypeList );
    }
}
