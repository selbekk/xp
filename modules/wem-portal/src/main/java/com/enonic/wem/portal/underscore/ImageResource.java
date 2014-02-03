package com.enonic.wem.portal.underscore;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.core.image.ImageHelper;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;

@Path("{mode}/{path:.+}/_/image/{fileName:.+}")
public final class ImageResource
    extends AbstractImageResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("fileName")
    protected String fileName;

    @QueryParam("filter")
    protected String filter;

    @QueryParam("background")
    protected String backgroundColor;

    @QueryParam("quality")
    protected String quality;

    @Inject
    protected ImageFilterBuilder imageFilterBuilder;


    @Override
    String getFilterParam()
    {
        return this.filter;
    }

    @Override
    String getQualityParam()
    {
        return quality;
    }

    @Override
    String getBackgroundColorParam()
    {
        return backgroundColor;
    }

    @GET
    public Response getResource()
        throws IOException
    {
        final ContentPath path = ContentPath.from( contentPath );
        final Content content = getContent( path );

        final Attachment attachment = getAttachment( content.getId(), fileName );

        final Blob blob = getBlob( attachment.getBlobKey() );
        if ( blob == null )
        {
            throw new RuntimeException( "Blob not found: " + attachment.getBlobKey() );
        }

        final BufferedImage contentImage = toBufferedImage( blob.getStream() );

        final String format = getFormat( this.fileName );

        final BufferedImage image = applyFilters( contentImage, format );

        byte[] imageData = ImageHelper.writeImage( image, format, resolveQuality() );

        return Response.ok( imageData, attachment.getMimeType() ).build();
    }
}
