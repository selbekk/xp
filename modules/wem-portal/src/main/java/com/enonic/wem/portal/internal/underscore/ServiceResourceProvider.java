package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public final class ServiceResourceProvider
    implements ResourceProvider<ServiceResource>
{
    private ModuleService moduleService;

    private JsControllerFactory controllerFactory;

    @Override
    public Class<ServiceResource> getType()
    {
        return ServiceResource.class;
    }

    @Override
    public ServiceResource newResource()
    {
        final ServiceResource instance = new ServiceResource();
        instance.moduleService = this.moduleService;
        instance.controllerFactory = this.controllerFactory;
        return instance;
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    public void setControllerFactory( final JsControllerFactory controllerFactory )
    {
        this.controllerFactory = controllerFactory;
    }
}