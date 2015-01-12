package com.enonic.xp.portal.impl.resource.underscore;

import javax.ws.rs.Path;

import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.portal.impl.resource.asset.AssetResource;
import com.enonic.xp.portal.impl.resource.attachment.AttachmentResource;
import com.enonic.xp.portal.impl.resource.render.ComponentResource;
import com.enonic.xp.portal.impl.resource.image.ImageResource;
import com.enonic.xp.portal.impl.resource.service.ServiceResource;

public final class UnderscoreResource
    extends BaseSubResource
{
    @Path("service")
    public ServiceResource service()
    {
        return initResource( new ServiceResource() );
    }

    @Path("image")
    public ImageResource image()
    {
        return initResource( new ImageResource() );
    }

    @Path("public")
    public AssetResource asset()
    {
        return initResource( new AssetResource() );
    }

    @Path("attachment")
    public AttachmentResource attachment()
    {
        return initResource( new AttachmentResource() );
    }

    @Path("component")
    public ComponentResource component()
    {
        return initResource( new ComponentResource() );
    }
}