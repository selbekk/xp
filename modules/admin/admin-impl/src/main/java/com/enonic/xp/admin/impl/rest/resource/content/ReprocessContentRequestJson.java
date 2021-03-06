package com.enonic.xp.admin.impl.rest.resource.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

final class ReprocessContentRequestJson
{
    private final BranchContentPath sourceBranchPath;

    private final boolean skipChildren;

    @JsonCreator
    public ReprocessContentRequestJson( @JsonProperty("sourceBranchPath") final String sourceBranchPath,
                                        @JsonProperty("skipChildren") final boolean skipChildren )
    {
        Preconditions.checkNotNull( sourceBranchPath, "targetBranchPath not specified" );

        this.sourceBranchPath = BranchContentPath.from( sourceBranchPath );
        this.skipChildren = skipChildren;
    }

    public BranchContentPath getSourceBranchPath()
    {
        return sourceBranchPath;
    }

    public boolean isSkipChildren()
    {
        return skipChildren;
    }
}
