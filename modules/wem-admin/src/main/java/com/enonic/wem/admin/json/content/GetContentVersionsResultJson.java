package com.enonic.wem.admin.json.content;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.versioning.ContentVersion;
import com.enonic.wem.api.content.versioning.FindContentVersionsResult;

public class GetContentVersionsResultJson
{
    private Set<ContentVersionJson> contentVersions = Sets.newLinkedHashSet();

    private final long totalHits;

    private final long hits;

    private final int from;

    private final int size;

    public GetContentVersionsResultJson( final FindContentVersionsResult result )
    {
        this.totalHits = result.getTotalHits();
        this.hits = result.getHits();
        this.from = result.getFrom();
        this.size = result.getSize();

        for ( final ContentVersion contentVersion : result.getContentVersions() )
        {
            this.contentVersions.add( new ContentVersionJson( contentVersion ) );
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<ContentVersionJson> getContentVersions()
    {
        return contentVersions;
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public long getHits()
    {
        return hits;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }
}
