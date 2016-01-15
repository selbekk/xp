package com.enonic.xp.admin.ui.tool.simpleauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.google.common.base.Strings;

import com.enonic.xp.admin.ui.tool.UriScriptHelper;

public class SimpleAuthResponseWrapper
    extends HttpServletResponseWrapper
{
    private final HttpServletRequest request;

    private boolean redirected;

    public SimpleAuthResponseWrapper( final HttpServletRequest req, final HttpServletResponse response )
    {
        super( response );
        this.request = req;
    }

    @Override
    public void setStatus( final int sc )
    {
        if ( 403 == sc )
        {
            try
            {
                redirect();
            }
            catch ( UnsupportedEncodingException e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            super.setStatus( sc );
        }
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if ( redirected )
        {
            return new PrintWriter( new StringWriter() );
        }
        return super.getWriter();
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        if ( redirected )
        {
            return new ServletOutputStream()
            {
                @Override
                public boolean isReady()
                {
                    return true;
                }

                @Override
                public void setWriteListener( final WriteListener writeListener )
                {

                }

                @Override
                public void write( final int b )
                    throws IOException
                {

                }
            };
        }
        return super.getOutputStream();
    }

    @Override
    public void setHeader( final String name, final String value )
    {
        if ( !redirected )
        {
            super.setHeader( name, value );
        }
    }

    @Override
    public void sendError( final int sc )
        throws IOException
    {
        if ( 403 == sc )
        {
            redirect();
        }
        else
        {
            super.sendError( sc );
        }
    }

    @Override
    public void sendError( final int sc, final String msg )
        throws IOException
    {
        if ( 403 == sc )
        {
            redirect();
        }
        else
        {
            super.sendError( sc, msg );
        }
    }

    private void redirect()
        throws UnsupportedEncodingException
    {
        super.setStatus( 307 );
        StringBuffer uri = new StringBuffer( request.getRequestURI() );
        if ( !Strings.isNullOrEmpty( request.getQueryString() ) )
        {
            uri.append( "?" ).
                append( request.getQueryString() );
        }
        final String callbackUri = URLEncoder.encode( uri.toString(), "UTF-8" );

        super.setHeader( "Location",
                         UriScriptHelper.generateAdminToolUri( "com.enonic.xp.admin.ui", "login" ) + "?callback=" + callbackUri );
        redirected = true;
    }
}
