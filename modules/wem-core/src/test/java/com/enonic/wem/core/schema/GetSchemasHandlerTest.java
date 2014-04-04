package com.enonic.wem.core.schema;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.SchemaTypes;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class GetSchemasHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetSchemasHandler handler;

    private MixinService mixinService;

    private ContentTypeService contentTypeService;

    private RelationshipTypeService relationshipTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinService = Mockito.mock( MixinService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );
        relationshipTypeService = Mockito.mock( RelationshipTypeService.class );

        handler = new GetSchemasHandler();
        handler.setContext( this.context );
        handler.setMixinService( this.mixinService );
        handler.setRelationshipTypeService( this.relationshipTypeService );
        handler.setContentTypeService( this.contentTypeService );
    }

    @Test
    public void getSchemas()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            build();
        final ContentTypes contentTypes = ContentTypes.from( contentType );

        Mockito.when( contentTypeService.getAll( Mockito.isA( GetAllContentTypesParams.class ) ) ).thenReturn( contentTypes );

        final FormItemSet formItemSet = newFormItemSet().name( "address" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "street" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build();
        final Mixin mixin = newMixin().name( "address" ).
            addFormItem( formItemSet ).
            build();
        final Mixins mixins = Mixins.from( mixin );
        Mockito.when( mixinService.getAll() ).thenReturn( mixins );

        final RelationshipType relationshipType = newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( relationshipTypeService.getAll() ).thenReturn( relationshipTypes );

        // exercise
        final SchemaTypes command = Commands.schema().get();
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        assertEquals( 3, command.getResult().getSize() );
    }
}
