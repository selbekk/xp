package com.enonic.wem.core.content.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.content.UpdateContentTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.content.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateContentTypesHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new UpdateContentTypesHandler();
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( QualifiedContentTypeNames.from( QualifiedContentTypeName.structured() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType existingContentType = newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( QualifiedContentTypeName.structured() ).
            build();

        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( contentTypeDao.select( isA( QualifiedContentTypeNames.class ), any( Session.class ) ) ).thenReturn( contentTypes );

        UpdateContentTypes command = Commands.contentType().update().names( QualifiedContentTypeNames.from( "myModule:myContentType" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( QualifiedContentTypeName.structured() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        verify( contentTypeDao, atLeastOnce() ).update( Mockito.isA( ContentType.class ), Mockito.any( Session.class ) );
        assertEquals( (Integer) 1, command.getResult() );
    }


    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( QualifiedContentTypeNames.from( QualifiedContentTypeName.shortcut() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.SHORTCUT ) );

        ContentType existingContentType = newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "myModule" ) ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( QualifiedContentTypeName.structured() ).
            build();

        Mockito.when(
            contentTypeDao.select( eq( QualifiedContentTypeNames.from( "myModule:myContentType" ) ), any( Session.class ) ) ).thenReturn(
            ContentTypes.from( existingContentType ) );

        UpdateContentTypes command = Commands.contentType().update().names( QualifiedContentTypeNames.from( "myModule:myContentType" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( QualifiedContentTypeName.shortcut() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.handle( this.context, command );
    }

}
