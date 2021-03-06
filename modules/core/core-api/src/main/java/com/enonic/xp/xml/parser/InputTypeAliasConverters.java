package com.enonic.xp.xml.parser;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

import com.enonic.xp.inputtype.InputTypeName;

@Beta
final class InputTypeAliasConverters
{
    public static InputTypeAliasConverter DEFAULT_CONVERTER = ( s ) -> s;

    private final Map<InputTypeName, InputTypeAliasConverter> map;

    private final static InputTypeAliasConverters INSTANCE = new InputTypeAliasConverters();

    private InputTypeAliasConverters()
    {
        this.map = Maps.newConcurrentMap();
        this.map.put( InputTypeName.CONTENT_SELECTOR, ContentSelectorAliasConverter.INSTANCE );
        this.map.put( InputTypeName.IMAGE_SELECTOR, ContentSelectorAliasConverter.INSTANCE );
    }

    public static InputTypeAliasConverter getConverter( final InputTypeName inputTypeName )
    {
        InputTypeAliasConverter result = INSTANCE.map.get( inputTypeName );
        return result != null ? result : DEFAULT_CONVERTER;
    }

    public static String convert( final InputTypeName inputTypeName, final String alias )
    {
        return getConverter( inputTypeName ).convert( alias );
    }

}
