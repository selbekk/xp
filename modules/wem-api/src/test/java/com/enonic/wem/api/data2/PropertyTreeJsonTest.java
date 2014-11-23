package com.enonic.wem.api.data2;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.support.JsonTestHelper;
import com.enonic.wem.api.util.GeoPoint;

import static org.junit.Assert.*;

public class PropertyTreeJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public PropertyTreeJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    private PropertyTree createPropertyTree_with_all_types()
    {
        PropertyTree tree = new PropertyTree();
        tree.addString( "singleString", "a" );
        tree.addString( "singleHtmlPart", "<h1>Hello</h1>" );
        tree.addXml( "singleXML", "<xml>Hello</xml>" );
        tree.addBoolean( "singleBoolean", true );
        tree.addLong( "singleLong", 1L );
        tree.addDouble( "singleDouble", 1.1 );
        tree.addGeoPoint( "singleGeoPoint", GeoPoint.from( "1.1,-2.2" ) );
        tree.addLocalDate( "singleLocalDate", LocalDate.of( 2006, 1, 8 ) );
        tree.addLocalDateTime( "singleLocalDateTime", LocalDateTime.of( 2006, 1, 8, 12, 0, 0 ) );
        tree.addLocalTime( "singleLocalTime", LocalTime.of( 12, 0, 0 ) );
        tree.addInstant( "singleInstant", Instant.parse( "2007-12-03T10:15:30.00Z" ) );
        PropertySet singleSet = tree.addSet( "singleSet" );
        singleSet.addLong( "long", 1L );
        PropertySet setWithinSet = singleSet.addSet( "setWithinSet" );
        setWithinSet.addLong( "long", 1L );

        tree.addStrings( "arrayString", "a", "b" );
        tree.addHtmlParts( "arrayHtmlPart", "<h1>Hello</h1>", "<h1>World</h1>" );
        tree.addXmls( "arrayXML", "<xml>Hello</xml>", "<xml>World</xml>" );
        tree.addBooleans( "arrayBoolean", true, false );
        tree.addLongs( "arrayLong", 1L, 2L );
        tree.addDoubles( "arrayDouble", 1.1, 1.2 );
        tree.addLocalDates( "arrayLocalDates", LocalDate.of( 2006, 1, 8 ), LocalDate.of( 2015, 1, 31 ) );
        tree.addLocalDateTimes( "arrayLocalDateTimes", LocalDateTime.of( 2006, 1, 8, 12, 0, 0 ),
                                LocalDateTime.of( 2015, 1, 31, 12, 0, 0 ) );
        tree.addGeoPoints( "arrayGeoPoint", GeoPoint.from( "1.1,-2.2" ), GeoPoint.from( "-2.2,1.1" ) );
        PropertySet arraySet1 = tree.addSet( "arraySet" );
        arraySet1.addString( "string", "a" );
        arraySet1.addLongs( "long", 1L, 2L );
        PropertySet arraySet2 = tree.addSet( "arraySet" );
        arraySet2.addStrings( "string", "b", "c" );
        arraySet2.addLong( "long", 2L );

        return tree;
    }

    @Test
    public void serialization_equals_serialization_of_deserialization()
        throws IOException
    {
        PropertyTree tree = createPropertyTree_with_all_types();

        List<PropertyArrayJson> propertyArrayJson = PropertyTreeJson.toJson( tree );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( propertyArrayJson );

        System.out.println( expectedSerialization );

        // de-serialize
        List<PropertyArrayJson> parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, List.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        // verify serialization against serializationOfDeSerialization
        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialized_from_serialized()
        throws IOException
    {
        PropertyTree sourceTree = createPropertyTree_with_all_types();
        List<PropertyArrayJson> serializedTree = PropertyTreeJson.toJson( sourceTree );

        // exercise
        PropertyTree tree = PropertyTreeJson.fromJson( serializedTree );

        // verify
        assertEquals( "a", tree.getString( "singleString" ) );
        assertEquals( "<h1>Hello</h1>", tree.getString( "singleHtmlPart" ) );
        assertEquals( "<xml>Hello</xml>", tree.getString( "singleXML" ) );
        assertEquals( true, tree.getBoolean( "singleBoolean" ) );
        assertEquals( Long.valueOf( 1L ), tree.getLong( "singleLong" ) );
        assertEquals( Double.valueOf( 1.1 ), tree.getDouble( "singleDouble" ) );
        assertEquals( GeoPoint.from( "1.1,-2.2" ), tree.getGeoPoint( "singleGeoPoint" ) );
        assertEquals( LocalDate.of( 2006, 1, 8 ), tree.getLocalDate( "singleLocalDate" ) );
        assertEquals( LocalDateTime.of( 2006, 1, 8, 12, 0, 0 ), tree.getLocalDateTime( "singleLocalDateTime" ) );

        assertEquals( "a", tree.getString( "arrayString" ) );
        assertEquals( "b", tree.getString( "arrayString[1]" ) );
        assertEquals( "a", tree.getString( "arraySet[0].string" ) );
        assertEquals( "b", tree.getString( "arraySet[1].string" ) );
        assertEquals( "c", tree.getString( "arraySet[1].string[1]" ) );

        assertEquals( sourceTree.toString(), tree.toString() );
    }

    @Test
    public void serialized_as_JsonNode()
        throws IOException
    {
        PropertyTree tree = createPropertyTree_with_all_types();

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( PropertyTreeJson.toJson( tree ) );

        // de-serialize
        List<PropertyArrayJson> parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, List.class );

        // verify de-serialization against static file
        jsonTestHelper.assertJsonEquals2( jsonTestHelper.loadTestJson( "all-types.json" ), jsonTestHelper.objectToJson( parsedData ) );
    }

}