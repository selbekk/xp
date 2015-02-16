package com.enonic.xp.content;

import com.enonic.xp.branch.Branch;

public class CompareContentsParams
{
    private final ContentIds contentIds;

    private final Branch target;

    public CompareContentsParams( final ContentIds contentIds, final Branch target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
    }
}