package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public final class PortalUrlBuildersHelper
{
    private static String systemParam( final Multimap<String, String> params, final String name )
    {
        final Collection<String> values = params.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    public static GeneralUrlBuilder apply( final GeneralUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.path( systemParam( params, "_path" ) );
        builder.params( params );
        return builder;
    }

    public static AssetUrlBuilder apply( final AssetUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.path( systemParam( params, "_path" ) );
        builder.params( params );
        return builder;
    }

    public static PageUrlBuilder apply( final PageUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.params( params );
        return builder;
    }

    public static AttachmentUrlBuilder apply( final AttachmentUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.params( params );
        return builder;
    }

    public static ComponentUrlBuilder apply( final ComponentUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.component( systemParam( params, "_component" ) );
        builder.params( params );
        return builder;
    }

    public static ImageUrlBuilder apply( final ImageUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.imageId( systemParam( params, "_id" ) );
        builder.imageName( systemParam( params, "_name" ) );
        builder.quality( systemParam( params, "_quality" ) );
        builder.filter( systemParam( params, "_filter" ) );
        builder.background( systemParam( params, "_background" ) );
        builder.params( params );
        return builder;
    }

    public static ServiceUrlBuilder apply( final ServiceUrlBuilder builder, final Multimap<String, String> params )
    {
        builder.service( systemParam( params, "_service" ) );
        builder.params( params );
        return builder;
    }

    public static Multimap<String, String> toParamMap( final String... params )
    {
        return toParamMap( Lists.newArrayList( params ) );
    }

    public static Multimap<String, String> toParamMap( final List<String> params )
    {
        final Multimap<String, String> map = HashMultimap.create();
        for ( final String param : params )
        {
            addParam( map, param );
        }

        return map;
    }

    private static void addParam( final Multimap<String, String> map, final String param )
    {
        final int pos = param.indexOf( '=' );
        if ( ( pos <= 0 ) || ( pos >= param.length() ) )
        {
            return;
        }

        final String key = param.substring( 0, pos ).trim();
        final String value = param.substring( pos + 1 ).trim();
        map.put( key, value );
    }
}