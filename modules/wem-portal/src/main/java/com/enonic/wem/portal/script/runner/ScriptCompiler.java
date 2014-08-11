package com.enonic.wem.portal.script.runner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.wem.api.resource.Resource;

final class ScriptCompiler
{
    private final Cache<String, Script> cache;

    public ScriptCompiler()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 1000 ).build();
    }

    public Script compile( final Context context, final Resource source )
    {
        final String key = source.getKey().toString() + "_" + source.getTimestamp();
        final Script script = this.cache.getIfPresent( key );
        if ( script != null )
        {
            return script;
        }

        final Script compiled = doCompile( context, source );
        this.cache.put( key, compiled );
        return compiled;
    }

    private Script doCompile( final Context context, final Resource source )
    {
        return context.compileString( source.readString(), source.getKey().toString(), 1, null );
    }
}
