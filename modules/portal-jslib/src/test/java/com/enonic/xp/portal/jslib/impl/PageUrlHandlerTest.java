package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.xp.portal.jslib.AbstractHandlerTest;

public class PageUrlHandlerTest
    extends AbstractHandlerTest
{
    @Override
    protected CommandHandler createHandler()
    {
        return new PageUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
