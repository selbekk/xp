package com.enonic.xp.portal.impl.script;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.impl.script.invoker.CommandInvokerImpl;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlRegistry;
import com.enonic.xp.resource.ResourceUrlTestHelper;

public abstract class AbstractScriptTest
{
    private final static ModuleKey MYMODULE_KEY = ModuleKey.from( "mymodule" );

    protected final ScriptServiceImpl scriptService;

    protected final CommandInvokerImpl invoker;

    public AbstractScriptTest()
    {
        this.invoker = new CommandInvokerImpl();

        this.scriptService = new ScriptServiceImpl();
        this.scriptService.addGlobalVariable( "assert", new AssertHelper() );
        this.scriptService.setInvoker( this.invoker );

        final ResourceUrlRegistry urlRegistry = ResourceUrlTestHelper.mockModuleScheme();
        urlRegistry.modulesClassLoader( getClass().getClassLoader() );
    }

    protected final void addHandler( final CommandHandler handler )
    {
        this.invoker.addHandler( handler );
    }

    protected final void removeHandler( final CommandHandler handler )
    {
        this.invoker.removeHandler( handler );
    }

    protected final ScriptExports runTestScript( final String name )
    {
        return runTestScript( ResourceKey.from( MYMODULE_KEY, name ) );
    }

    protected final ScriptExports runTestScript( final ResourceKey key )
    {
        return this.scriptService.execute( key );
    }
}
