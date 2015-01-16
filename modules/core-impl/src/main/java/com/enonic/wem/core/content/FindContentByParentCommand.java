package com.enonic.wem.core.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.api.content.FindContentByParentResult;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;

final class FindContentByParentCommand
    extends AbstractContentCommand
{
    private final FindContentByParentParams params;

    private FindContentByParentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create( final FindContentByParentParams params )
    {
        return new Builder( params );
    }

    FindContentByParentResult execute()
    {
        boolean useWorkspaceOrdering = false;

        final FindNodesByParentParams.Builder findNodesParam = FindNodesByParentParams.create();

        if ( params.getParentPath() == null && params.getParentId() == null )
        {
            final NodePath parentPath = ContentNodeHelper.CONTENT_ROOT_NODE.asAbsolute();
            findNodesParam.parentPath( parentPath );

            useWorkspaceOrdering = params.getChildOrder() == null || params.getChildOrder().isEmpty();
        }
        else if ( params.getParentPath() != null )
        {
            final NodePath parentPath = ContentNodeHelper.translateContentPathToNodePath( params.getParentPath() );
            findNodesParam.parentPath( parentPath );
        }
        else
        {
            final NodeId parentId = NodeId.from( params.getParentId().toString() );
            findNodesParam.parentId( parentId );
        }

        findNodesParam.
            from( params.getFrom() ).
            size( params.getSize() ).
            childOrder( useWorkspaceOrdering ? ContextAccessor.current().getWorkspace().getChildOrder() : params.getChildOrder() ).
            build();
        final FindNodesByParentResult result = nodeService.findByParent( findNodesParam.build() );

        final Nodes nodes = result.getNodes();

        Contents contents = this.translator.fromNodes( nodes );

        return FindContentByParentResult.create().
            contents( contents ).
            totalHits( result.getTotalHits() ).
            hits( result.getHits() ).
            build();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final FindContentByParentParams params;

        public Builder( final FindContentByParentParams params )
        {
            this.params = params;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
        }

        public FindContentByParentCommand build()
        {
            validate();
            return new FindContentByParentCommand( this );
        }
    }

}