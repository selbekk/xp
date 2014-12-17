package com.enonic.xp.portal.thymeleaf.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;

import static org.junit.Assert.*;

public class RenderViewHandlerTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final PortalContext context = Mockito.mock( PortalContext.class );
        Mockito.when( context.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( context.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( context.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( context.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );
        Mockito.when( context.getModule() ).thenReturn( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        Mockito.when( context.getContent() ).thenReturn( content );
        PortalContextAccessor.set( context );

        addHandler( new RenderViewHandler() );
    }

    private Object execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "thymeleaf-test.js" );
        return exports.executeMethod( method ).getValue();
    }

    private void executeException( final String method, final String expectedMessage )
        throws Exception
    {
        try
        {
            execute( method );
            fail( "Expected to fail with exception" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( expectedMessage, e.getMessage() );
        }
        catch ( final Exception e )
        {
            fail( "Expected ResourceProblemException but got " + e.getClass().getName() );
        }
    }

    @Test
    public void renderNoView()
        throws Exception
    {
        executeException( "noViewTest", "Parameter [view] is required" );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = execute( "renderTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/modules/mymodule/view/test-result.html" ), Charsets.UTF_8 );
        assertEquals( expected, result );
    }

    @Test
    public void functionsTest()
        throws Exception
    {
        final String result = execute( "functionsTest" ).toString();
        final String expected =
            Resources.toString( getClass().getResource( "/modules/mymodule/view/functions-result.html" ), Charsets.UTF_8 );
        assertEquals( expected, result );
    }
}