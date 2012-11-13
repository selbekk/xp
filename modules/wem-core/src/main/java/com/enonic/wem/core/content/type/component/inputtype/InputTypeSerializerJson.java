package com.enonic.wem.core.content.type.component.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.component.inputtype.BaseInputType;
import com.enonic.wem.core.content.AbstractSerializerJson;
import com.enonic.wem.core.content.JsonParserUtil;

public class InputTypeSerializerJson
    extends AbstractSerializerJson<BaseInputType>
{

    @Override
    public JsonNode serialize( final BaseInputType baseInputType, final ObjectMapper objectMapper )
    {
        final ObjectNode inputNode = objectMapper.createObjectNode();
        inputNode.put( "name", baseInputType.getName() );
        inputNode.put( "builtIn", baseInputType.isBuiltIn() );
        return inputNode;
    }

    public BaseInputType parse( final JsonNode node )
    {
        final String className = JsonParserUtil.getStringValue( "name", node );
        final boolean builtIn = JsonParserUtil.getBooleanValue( "builtIn", node );
        return InputTypeFactory.instantiate( className, builtIn );
    }
}
