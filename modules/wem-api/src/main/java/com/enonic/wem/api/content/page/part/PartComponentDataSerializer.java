package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.AbstractDescriptorBasedPageComponentDataSerializer;
import com.enonic.wem.api.data.DataSet;

public class PartComponentDataSerializer
    extends AbstractDescriptorBasedPageComponentDataSerializer<PartComponent, PartComponent>
{

    public DataSet toData( final PartComponent component )
    {
        final DataSet asData = new DataSet( PartComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        return asData;
    }

    public PartComponent fromData( final DataSet asData )
    {
        PartComponent.Builder component = PartComponent.newPartComponent();
        applyPageComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return PartDescriptorKey.from( s );
    }

}
