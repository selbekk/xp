package com.enonic.xp.content.page.region;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

public class LayoutDescriptor
    extends Descriptor<DescriptorKey>
{
    private final RegionDescriptors regions;

    private LayoutDescriptor( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.regions = builder.regions;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    @Override
    public ResourceKey getComponentPath()
    {
        final DescriptorKey key = this.getKey();
        return ResourceKey.from( key.getModuleKey(), "cms/layouts/" + key.getName() );
    }

    public static LayoutDescriptor.Builder create()
    {
        return new Builder();
    }

    public static LayoutDescriptor.Builder copyOf( final LayoutDescriptor layoutDescriptor )
    {
        return new Builder( layoutDescriptor );
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getModuleKey(), "cms/layouts/" + key.getName() + "/layout.xml" );
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, DescriptorKey>
    {
        private RegionDescriptors regions;

        private Builder()
        {
        }

        private Builder( final LayoutDescriptor layoutDescriptor )
        {
            super( layoutDescriptor );
            this.regions = layoutDescriptor.getRegions();
        }

        public Builder regions( final RegionDescriptors value )
        {
            this.regions = value;
            return this;
        }

        public LayoutDescriptor build()
        {
            return new LayoutDescriptor( this );
        }
    }
}