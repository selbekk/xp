package com.enonic.wem.web.rpc.processor;

import org.codehaus.jackson.node.ObjectNode;

public final class WebRpcRequest
    extends WebRpcMessage
{
    private ObjectNode params;

    public ObjectNode getParams()
    {
        return this.params;
    }

    public void setParams( final ObjectNode params )
    {
        this.params = params;
    }
}
