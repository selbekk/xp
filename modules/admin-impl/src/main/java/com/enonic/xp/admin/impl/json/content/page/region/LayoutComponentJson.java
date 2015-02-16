package com.enonic.xp.admin.impl.json.content.page.region;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.region.ComponentName;
import com.enonic.xp.content.page.region.LayoutComponent;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;

import static com.enonic.xp.content.page.region.LayoutComponent.newLayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends DescriptorBasedComponentJson<LayoutComponent>
{
    private final LayoutComponent layout;

    private final LayoutRegionsJson regionsJson;

    @JsonCreator
    public LayoutComponentJson( @JsonProperty("name") final String name, @JsonProperty("descriptor") final String descriptor,
                                @JsonProperty("config") final List<PropertyArrayJson> config,
                                final @JsonProperty("regions") List<RegionJson> regions )
    {
        super( newLayoutComponent().
            name( name != null ? ComponentName.from( name ) : null ).
            descriptor( descriptor != null ? DescriptorKey.from( descriptor ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            regions( regions != null ? new LayoutRegionsJson( regions ).getLayoutRegions() : null ).
            build() );

        this.layout = getComponent();
        this.regionsJson = new LayoutRegionsJson( layout.getRegions() );
    }

    public LayoutComponentJson( final LayoutComponent component )
    {
        super( component );
        this.layout = component;
        this.regionsJson = new LayoutRegionsJson( component.getRegions() );
    }

    public List<RegionJson> getRegions()
    {
        return regionsJson.getRegions();
    }
}