package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class TemplatesTest
{

    @Test
    public void pageTemplate()
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            id( new PageTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            name( new PageTemplateName( "main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", pageTemplate.getId().toString() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "article" ) ) );
    }

    @Test
    public void partTemplate()
    {
        RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        PartTemplate partTemplate = PartTemplate.newPartTemplate().
            id( new PartTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            name( new PartTemplateName( "news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

    @Test
    public void layoutTemplate()
    {
        RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        LayoutTemplate partTemplate = LayoutTemplate.newLayoutTemplate().
            id( new LayoutTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            name( new LayoutTemplateName( "my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-layout.xml" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

    @Test
    public void siteTemplate()
    {
        SiteTemplate partTemplate = SiteTemplate.newSiteTemplate().
            id( new SiteTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            supportedContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", partTemplate.getId().toString() );
    }

}
