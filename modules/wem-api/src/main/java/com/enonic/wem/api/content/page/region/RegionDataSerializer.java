package com.enonic.wem.api.content.page.region;


import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentsDataSerializer;
import com.enonic.wem.api.data.DataId;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;

import static com.enonic.wem.api.content.page.region.Region.newRegion;

public class RegionDataSerializer
    extends AbstractDataSetSerializer<Region, Region>
{
    private static final DataId NAME = DataId.from( "name" );

    private static final String COMPONENTS = "components";

    private final String dataSetName;

    private final PageComponentsDataSerializer componentsSerializer = new PageComponentsDataSerializer();

    public RegionDataSerializer()
    {
        this.dataSetName = "region";
    }

    public DataSet toData( final Region region )
    {
        final DataSet asData = new DataSet( dataSetName );
        asData.setProperty( NAME, Value.newString( region.getName() ) );

        final DataSet componentsDataSet = new DataSet( COMPONENTS );
        componentsDataSet.addAll( componentsSerializer.toData( region.getComponents() ) );
        asData.add( componentsDataSet );

        return asData;
    }

    public Region fromData( final DataSet asData )
    {
        final Region.Builder region = newRegion();
        region.name( asData.getProperty( NAME ).getString() );

        final DataSet componentsDataSet = asData.getDataSet( COMPONENTS );
        for ( PageComponent component : componentsSerializer.fromData( componentsDataSet.getData() ) )
        {
            region.add( component );
        }

        return region.build();
    }
}
