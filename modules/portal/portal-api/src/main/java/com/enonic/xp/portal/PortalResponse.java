package com.enonic.xp.portal;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ListMultimap;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.websocket.WebSocketConfig;
import com.enonic.xp.web.HttpStatus;

@Beta
public final class PortalResponse
{
    private final HttpStatus status;

    private final MediaType contentType;

    private final Object body;

    private final ImmutableMap<String, String> headers;

    private final boolean postProcess;

    private final ImmutableListMultimap<HtmlTag, String> contributions;

    private final ImmutableList<Cookie> cookies;

    private final boolean applyFilters;

    private final WebSocketConfig webSocket;

    private PortalResponse( final Builder builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers.build();
        this.postProcess = builder.postProcess;
        this.contributions = builder.contributions.build();
        this.cookies = builder.cookies.build();
        this.applyFilters = builder.applyFilters;
        this.webSocket = builder.webSocket;
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public MediaType getContentType()
    {
        return this.contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public ImmutableMap<String, String> getHeaders()
    {
        return this.headers;
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    public ImmutableList<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.containsKey( tag ) ? this.contributions.get( tag ) : ImmutableList.of();
    }

    public boolean hasContributions()
    {
        return !this.contributions.isEmpty();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getAsString()
    {
        if ( this.body instanceof Map )
        {
            return convertToJson( this.body );
        }
        return ( this.body != null ) ? this.body.toString() : null;
    }

    private String convertToJson( final Object value )
    {
        try
        {
            return new ObjectMapper().writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public ImmutableList<Cookie> getCookies()
    {
        return cookies;
    }

    public WebSocketConfig getWebSocket()
    {
        return this.webSocket;
    }

    public boolean applyFilters()
    {
        return applyFilters;
    }

    public static Builder create( final PortalResponse source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private Object body;

        private ImmutableMap.Builder<String, String> headers;

        private MediaType contentType = MediaType.PLAIN_TEXT_UTF_8;

        private boolean postProcess = true;

        private ImmutableListMultimap.Builder<HtmlTag, String> contributions;

        private HttpStatus status = HttpStatus.OK;

        private ImmutableList.Builder<Cookie> cookies;

        private boolean applyFilters = true;

        private WebSocketConfig webSocket;

        private Builder()
        {
            clearHeaders();
            clearContributions();
            clearCookies();
        }

        private Builder( final PortalResponse source )
        {
            this.body = source.body;
            headers( source.headers );
            this.contentType = source.contentType;
            this.postProcess = source.postProcess;
            contributions( source.contributions );
            this.status = source.status;
            cookies( source.cookies );
            this.applyFilters = source.applyFilters;
            this.webSocket = source.webSocket;
        }

        public Builder body( final Object body )
        {
            this.body = body;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.putAll( headers );
            return this;
        }

        public Builder header( final String key, final String value )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.put( key, value );
            return this;
        }

        public Builder clearHeaders()
        {
            headers = ImmutableSortedMap.orderedBy( String.CASE_INSENSITIVE_ORDER );
            return this;
        }

        public Builder cookies( final List<Cookie> cookies )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.addAll( cookies );
            return this;
        }

        public Builder cookie( final Cookie cookie )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.add( cookie );
            return this;
        }

        public Builder clearCookies()
        {
            this.cookies = ImmutableList.builder();
            return this;
        }

        public Builder contentType( MediaType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder postProcess( final boolean postProcess )
        {
            this.postProcess = postProcess;
            return this;
        }

        public Builder contributions( final ListMultimap<HtmlTag, String> contributions )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( contributions );
            return this;
        }

        public Builder contribution( final HtmlTag tag, final String value )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.put( tag, value );
            return this;
        }

        public Builder contributionsFrom( final PortalResponse portalResponse )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( portalResponse.contributions );
            return this;
        }

        public Builder clearContributions()
        {
            this.contributions = ImmutableListMultimap.builder();
            return this;
        }

        public Builder status( final HttpStatus status )
        {
            this.status = status;
            return this;
        }

        public Builder applyFilters( final boolean applyFilters )
        {
            this.applyFilters = applyFilters;
            return this;
        }

        public Builder webSocket( final WebSocketConfig webSocket )
        {
            this.webSocket = webSocket;
            return this;
        }

        public PortalResponse build()
        {
            return new PortalResponse( this );
        }
    }
}
