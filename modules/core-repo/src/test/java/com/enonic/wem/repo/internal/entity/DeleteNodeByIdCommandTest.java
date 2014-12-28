package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class DeleteNodeByIdCommandTest
    extends AbstractNodeTest
{

    @Test
    public void delete()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        doDeleteNode( createdNode );

        assertNull( getNodeById( createdNode.id() ) );
    }

    @Test
    public void delete_with_children()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );
        refresh();

        final Node childNode = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "my-node" ).
            build() );
        refresh();

        final Node childChildNode = createNode( CreateNodeParams.create().
            parent( childNode.path() ).
            name( "my-node" ).
            build() );
        refresh();

        assertNotNull( getNodeById( parentNode.id() ) );
        assertNotNull( getNodeById( childNode.id() ) );
        assertNotNull( getNodeById( childChildNode.id() ) );

        doDeleteNode( parentNode );

        assertNull( getNodeById( parentNode.id() ) );
        assertNull( getNodeById( childNode.id() ) );
        assertNull( getNodeById( childChildNode.id() ) );
    }

    private Node doDeleteNode( final Node createdNode )
    {
        return DeleteNodeByIdCommand.create().
            nodeId( createdNode.id() ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }

}