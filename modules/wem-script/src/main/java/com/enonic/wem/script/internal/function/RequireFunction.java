package com.enonic.wem.script.internal.function;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.internal.ScriptExecutor;

public final class RequireFunction
    extends AbstractJSObject
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey script;

    private final ScriptExecutor executor;

    public RequireFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;
    }

    @Override
    public boolean isFunction()
    {
        return true;
    }

    @Override
    public boolean isStrictFunction()
    {
        return true;
    }

    @Override
    public Object call( final Object thiz, final Object... args )
    {
        if ( args.length != 1 )
        {
            throw new IllegalArgumentException( "require(..) must have one parameter" );
        }

        final String name = args[0].toString();
        final ResourceKey key = resolve( name );

        final Bindings bindings = this.executor.createBindings();
        return this.executor.executeRequire( bindings, key );
    }

    private ResourceKey resolve( final String name )
    {
        if ( !name.endsWith( SCRIPT_SUFFIX ) )
        {
            return resolve( name + SCRIPT_SUFFIX );
        }

        if ( name.startsWith( "/" ) )
        {
            return this.script.resolve( name );
        }

        if ( name.startsWith( "./" ) )
        {
            return this.script.resolve( "../" + name );
        }

        final ResourceKey resolved = this.script.resolve( "../" + name );
        if ( Resource.from( resolved ).exists() )
        {
            return resolved;
        }

        return this.script.resolve( "/lib/" + name );
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "require", this );
    }
}