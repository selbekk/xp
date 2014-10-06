package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.servlet.jaxrs.ResourceProvider;

public final class ImageResourceProvider
    implements ResourceProvider<ImageResource>
{
    private ImageFilterBuilder imageFilterBuilder;

    private AttachmentService attachmentService;

    private BlobService blobService;

    private ContentService contentService;

    @Override
    public Class<ImageResource> getType()
    {
        return ImageResource.class;
    }

    @Override
    public ImageResource newResource()
    {
        final ImageResource instance = new ImageResource();
        instance.imageFilterBuilder = this.imageFilterBuilder;
        instance.attachmentService = this.attachmentService;
        instance.blobService = this.blobService;
        instance.contentService = this.contentService;
        return instance;
    }

    public final void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }

    public final void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }

    public final void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}