package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentIdQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSummaryQueryResultJson;
import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByQueryResult;

public class FindContentByQuertResultJsonFactory
{
    public static AbstractContentQueryResultJson create( final FindContentByQueryResult contentQueryResult, final String expand,
                                                         final ContentIconUrlResolver iconUrlResolver,
                                                         final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        final AbstractContentQueryResultJson.Builder builder;

        final ContentListMetaData metadata = ContentListMetaData.create().
            totalHits( contentQueryResult.getTotalHits() ).
            hits( contentQueryResult.getHits() ).
            build();

        if ( Expand.FULL.matches( expand ) )
        {
            builder = ContentQueryResultJson.newBuilder( iconUrlResolver, contentPrincipalsResolver );
        }
        else if ( Expand.SUMMARY.matches( expand ) )
        {
            builder = ContentSummaryQueryResultJson.newBuilder( iconUrlResolver );
        }
        else
        {
            builder = ContentIdQueryResultJson.newBuilder();
        }

        addAggregations( contentQueryResult.getAggregations(), builder );
        addContents( contentQueryResult.getContents(), builder );
        setMetadata( metadata, builder );

        return builder.build();
    }

    private static void addContents( final Contents contents, final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final Content content : contents )
        {
            builder.addContent( content );
        }
    }

    private static void addAggregations( final Aggregations aggregations, final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof BucketAggregation )
            {
                builder.addAggregation( new BucketAggregationJson( (BucketAggregation) aggregation ) );
            }
        }
    }

    private static void setMetadata( final ContentListMetaData metadata, final AbstractContentQueryResultJson.Builder builder )
    {
        builder.setMetadata( metadata );
    }
}
