package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;

public class FindNodesByParentCommand
    extends AbstractNodeCommand
{
    private final FindNodesByParentParams params;

    private FindNodesByParentCommand( Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public FindNodesByParentResult execute()
    {
        NodePath parentPath = params.getParentPath();
        if ( parentPath == null )
        {
            parentPath = getPathFromId( params.getParentId() );
            if ( parentPath == null )
            {
                return FindNodesByParentResult.create().nodes( Nodes.empty() ).totalHits( 0l ).hits( 0l ).build();
            }
        }

        final ChildOrder order = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            workspaceService( this.queryService ).
            nodePath( parentPath ).
            childOrder( params.getChildOrder() ).
            build().
            resolve();

        final NodeQuery query = createByPathQuery( order, parentPath );

        final NodeQueryResult nodeQueryResult = this.queryService.find( query, IndexContext.from( ContextAccessor.current() ) );

        final Nodes nodes = doGetByIds( nodeQueryResult.getNodeIds(), order.getOrderExpressions(), true );

        return FindNodesByParentResult.create().
            nodes( nodes ).
            totalHits( nodeQueryResult.getTotalHits() ).
            hits( nodeQueryResult.getHits() ).
            build();
    }

    private NodeQuery createByPathQuery( final ChildOrder order, final NodePath parentPath )
    {
        return NodeQuery.create().
            parent( parentPath ).
            query( new QueryExpr( order.getOrderExpressions() ) ).
            from( params.getFrom() ).
            size( params.getSize() ).
            countOnly( params.isCountOnly() ).
            build();
    }

    private NodePath getPathFromId( final NodeId nodeId )
    {
        final Context context = ContextAccessor.current();
        final NodeVersionId currentVersion = this.queryService.get( nodeId, IndexContext.from( context ) );
        if ( currentVersion == null )
        {
            return null;
        }
        final Node currentNode = nodeDao.getByVersionId( currentVersion );
        return currentNode == null ? null : currentNode.path();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private FindNodesByParentParams params;

        public Builder()
        {
            super();
        }

        public Builder params( FindNodesByParentParams params )
        {
            this.params = params;
            return this;
        }

        public FindNodesByParentCommand build()
        {
            return new FindNodesByParentCommand( this );
        }
    }
}