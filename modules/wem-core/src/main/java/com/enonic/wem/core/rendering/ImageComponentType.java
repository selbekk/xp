package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.image.Image;

public class ImageComponentType
    implements ComponentType<Image>
{
    @Override
    public RenderingResult execute( final Image image, final Context context )
    {
        return null;
    }
}
