package com.enonic.wem.portal;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutRegions;

public interface PortalContext
{
    public PortalRequest getRequest();

    public PortalResponse getResponse();

    public Content getContent();

    public Content getSiteContent();

    public PageTemplate getPageTemplate();

    public PageRegions getPageRegions();

    public LayoutRegions getLayoutRegions();

    public PageComponent getComponent();

    // TODO: Should be ModuleKey here
    public String getResolvedModule();
}