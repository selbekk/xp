package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class GetNearestSiteCommandTest
{
    private ContentService contentService;

    public final Workspace testWorkspace = Workspace.from( "test" );

    private final Context testContext = Context.create().
        workspace( testWorkspace ).
        repositoryId( RepositoryId.from( "testing" ) ).
        build();

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
    }

    @Test
    public void get_nearest_site_content_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Site site = Site.newSite().path( "/mycontent" ).id( contentId ).build();
        Mockito.when( this.contentService.getById( contentId, testContext ) ).thenReturn( site );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            context( this.testContext ).
            contentService( this.contentService ).
            build();

        assertEquals( site, command.execute() );
    }

    @Test
    public void get_nearest_site_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final ContentPath parentPath = ContentPath.from( "/aaa" );

        final Content content = Content.newContent().
            id( contentId ).
            name( "name" ).
            parentPath( parentPath ).
            build();

        Mockito.when( this.contentService.getById( contentId, testContext ) ).
            thenReturn( content );

        final Site parent = Site.newSite().
            path( "/mycontent" ).
            id( ContentId.from( "bbb" ) ).
            build();

        Mockito.when( this.contentService.getByPath( parentPath, testContext ) ).
            thenReturn( parent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            context( this.testContext ).
            contentService( this.contentService ).
            build();

        assertEquals( parent, command.execute() );
    }

    @Test
    public void get_nearest_site_parent_of_parent_is_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );

        final Content content = Content.newContent().
            id( contentId ).
            name( "name" ).
            parentPath( ContentPath.from( "/aaa" ) ).
            build();

        Mockito.when( this.contentService.getById( contentId, testContext ) ).
            thenReturn( content );

        final Content parent = Content.newContent().
            id( ContentId.from( "bbb" ) ).
            name( "renome" ).
            parentPath( ContentPath.from( "/bbb" ) ).
            build();

        final Site parentOfParent = Site.newSite().
            path( "/mycontent" ).
            id( ContentId.from( "ccc" ) ).
            build();

        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ), Mockito.isA( Context.class ) ) ).
            thenReturn( parent ).
            thenReturn( parentOfParent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            context( this.testContext ).
            contentService( this.contentService ).
            build();

        assertEquals( parentOfParent, command.execute() );
    }

    @Test
    public void get_nearest_site_no_nearest_site()
        throws Exception
    {
        final ContentId contentId = ContentId.from( "aaa" );
        final Content content = Content.newContent().id( contentId ).name( "name" ).parentPath( ContentPath.from( "/aaa" ) ).build();
        Mockito.when( this.contentService.getById( contentId, testContext ) ).thenReturn( content );

        final ContentPath contentPath = ContentPath.from( "/mycontent" );
        final Content parent = Content.newContent().path( contentPath ).id( ContentId.from( "bbb" ) ).build();
        Mockito.when( this.contentService.getByPath( Mockito.isA( ContentPath.class ), Mockito.isA( Context.class ) ) ).thenReturn(
            parent );

        final GetNearestSiteCommand command = GetNearestSiteCommand.create().
            contentId( contentId ).
            context( this.testContext ).
            contentService( this.contentService ).
            build();

        assertNull( command.execute() );
    }
}