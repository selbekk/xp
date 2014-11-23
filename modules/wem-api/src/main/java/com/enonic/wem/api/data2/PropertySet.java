package com.enonic.wem.api.data2;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.util.GeoPoint;

public final class PropertySet
{
    private PropertyTree propertyTree;

    private Property property;

    private final LinkedHashMap<String, PropertyArray> propertyArrayByName = new LinkedHashMap<>();

    /**
     * If set to true, then the next added or set property with a null value must be ignored.
     */
    private boolean ifNotNull = false;

    PropertySet( final PropertyTree propertyTree )
    {
        this.propertyTree = propertyTree;
    }

    private PropertySet( final PropertySet source, final PropertyTree tree )
    {
        this.propertyTree = tree;
        for ( final PropertyArray array : source.propertyArrayByName.values() )
        {
            this.propertyArrayByName.put( array.getName(), array.copy( tree, this ) );
        }
    }

    PropertyTree getTree()
    {
        return this.propertyTree;
    }

    Property getProperty()
    {
        return property;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PropertySet ) )
        {
            return false;
        }

        final PropertySet that = (PropertySet) o;

        if ( !propertyArrayByName.equals( that.propertyArrayByName ) )
        {
            return false;
        }

        return true;
    }

    public String toString()
    {
        final StringBuilder s = new StringBuilder();
        s.append( "\n" );
        final Collection<PropertyArray> propertyArrays = propertyArrayByName.values();
        int size = propertyArrays.size();
        int count = 0;
        for ( PropertyArray propertyArray : propertyArrays )
        {
            s.append( propertyArray );
            if ( count++ < size - 1 )
            {
                s.append( ",\n" );
            }
        }

        return s.toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( propertyArrayByName );
    }

    /**
     * Makes a copy of this PropertySet and attach it to the given PropertyTree.
     */
    PropertySet copy( final PropertyTree tree )
    {
        return new PropertySet( this, tree );
    }

    public PropertyTree toTree()
    {
        return new PropertyTree( this );
    }

    public int countAncestors()
    {
        return this.property.countAncestors();
    }

    void setProperty( final Property property )
    {
        this.property = property;
    }

    /**
     * If invoked, then the next added or set property with a null will be ignored.
     * The second add or set after this method is called will not be affected.
     */
    public PropertySet ifNotNull()
    {
        ifNotNull = true;
        return this;
    }

    /**
     * Creates a new PropertySet attached to the same PropertyTree as this.
     * However it is not added to this PropertySet.
     */
    public PropertySet newSet()
    {
        return new PropertySet( propertyTree );
    }

    void setPropertyTree( final PropertyTree propertyTree )
    {
        if ( this.propertyTree != null && propertyTree != this.propertyTree )
        {
            throw new IllegalArgumentException(
                "PropertySet already belongs to a ValueTree. Detach it from existing PropertyTree before adding it to another: " +
                    this.propertyTree );
        }
        this.propertyTree = propertyTree;
    }

    public PropertySet detach()
    {
        this.propertyTree = null;
        for ( final PropertyArray propertyArray : propertyArrayByName.values() )
        {
            propertyArray.detach();
        }
        return this;
    }

    public int countProperties( final String name )
    {
        final PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null )
        {
            return 0;
        }
        return array.size();
    }

    void add( final Property property )
    {

        final PropertyArray array = getOrCreatePropertyArray( property.getName(), property.getValueType() );
        array.addProperty( property );
        propertyTree.registerProperty( property );
        if ( property.getValue().isPropertySet() )
        {
            final PropertySet set = property.getSet();
            set.setPropertyTree( propertyTree );
        }
    }

    public final Property addProperty( final String name, final Value value )
    {
        if ( ifNotNull && value.isNull() )
        {
            ifNotNull = false;
            return null;
        }

        final PropertyArray array = getOrCreatePropertyArray( name, value.getType() );
        final Property property = array.addValue( value );
        propertyTree.registerProperty( property );
        if ( value.getObject() instanceof PropertySet )
        {
            final PropertySet set = (PropertySet) value.getObject();
            set.setPropertyTree( propertyTree );
        }
        return property;
    }

    public final Property setProperty( final String path, final Value value )
    {
        return setProperty( PropertyPath.from( path ), value );
    }

    public final Property setProperty( final PropertyPath path, final Value value )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final PropertySet propertySet = getOrCreateSet( firstElement.getName(), firstElement.getIndex() );
            return propertySet.setProperty( path.removeFirstPathElement(), value );
        }
        else
        {
            return setProperty( firstElement.getName(), firstElement.getIndex(), value );
        }
    }

    public final Property setProperty( final String name, final int index, final Value value )
    {
        if ( ifNotNull && value.isNull() )
        {
            ifNotNull = false;
            return null;
        }

        final PropertyArray array = getOrCreatePropertyArray( name, value.getType() );
        final Property property = array.setValue( index, value );
        propertyTree.registerProperty( property );
        return property;
    }

    private PropertySet getOrCreateSet( final String name, final int index )
    {
        final Property existingProperty = getProperty( name, index );
        if ( existingProperty == null )
        {
            final PropertySet set = new PropertySet( propertyTree );
            setProperty( name, index, Value.newData( set ) );
            return set;
        }
        else
        {
            return existingProperty.getSet();
        }
    }

    private PropertyArray getOrCreatePropertyArray( final String name, final ValueType type )
    {
        PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null )
        {
            array = new PropertyArray( propertyTree, this, name, type );
            this.propertyArrayByName.put( name, array );
        }
        return array;
    }

    public void setValues( final String path, final Iterable<Value> values )
    {
        setValues( PropertyPath.from( path ), values );
    }

    public void setValues( final PropertyPath path, final Iterable<Value> values )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return;
            }
            final PropertySet set = property.getSet();
            set.setValues( path.removeFirstPathElement(), values );
        }
        else
        {
            int index = 0;
            for ( final Value value : values )
            {
                setProperty( firstElement.getName(), index++, value );
            }
        }
    }

    public void removeProperty( final String path )
    {
        removeProperty( PropertyPath.from( path ) );
    }

    public void removeProperty( final PropertyPath path )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return;
            }
            final PropertySet set = property.getSet();
            set.removeProperty( path.removeFirstPathElement() );
        }
        else
        {
            removeProperty( firstElement );
        }
    }

    private void removeProperty( final PropertyPath.Element element )
    {
        final PropertyArray propertyArray = propertyArrayByName.get( element.getName() );
        if ( propertyArray == null )
        {
            return;
        }

        propertyArray.remove( element.getIndex() );
    }

    public void removeProperties( final String name )
    {
        final PropertyArray propertyArray = propertyArrayByName.get( name );
        if ( propertyArray == null )
        {
            return;
        }
        propertyArray.removeAll();
        propertyArrayByName.remove( name );
    }

    public boolean hasProperty( final String path )
    {
        return hasProperty( PropertyPath.from( path ) );
    }

    public boolean hasProperty( final PropertyPath path )
    {
        return getProperty( path ) != null;
    }

    public boolean hasProperty( final String name, final int index )
    {
        return getProperty( name, index ) != null;
    }

    public Property getProperty( final String path )
    {
        return getProperty( PropertyPath.from( path ) );
    }

    public Property getProperty( final PropertyPath path )
    {
        final PropertyPath.Element firstElement = path.getFirstElement();
        if ( path.elementCount() > 1 )
        {
            final Property property = getProperty( firstElement );
            if ( property == null )
            {
                return null;
            }
            final PropertySet set = property.getSet();
            return set.getProperty( path.removeFirstPathElement() );
        }
        else
        {
            return getProperty( firstElement );
        }
    }

    public Property getProperty( final String name, final int index )
    {
        Property.checkName( name );
        final PropertyArray array = this.propertyArrayByName.get( name );
        if ( array == null )
        {
            return null;
        }
        return array.get( index );
    }

    public boolean isNull( final String path )
    {
        return isNull( PropertyPath.from( path ) );
    }

    public boolean isNull( final PropertyPath path )
    {
        return !isNotNull( path );
    }

    public boolean isNotNull( final String path )
    {
        return isNotNull( PropertyPath.from( path ) );
    }

    public boolean isNotNull( final PropertyPath path )
    {
        final Property property = getProperty( path );
        if ( property == null )
        {
            return false;
        }

        return !property.hasNullValue();
    }

    public String[] getPropertyNames()
    {
        final Set<String> keySet = propertyArrayByName.keySet();
        final String[] propertyNames = new String[keySet.size()];
        int index = 0;
        for ( final String key : keySet )
        {
            propertyNames[index++] = key;
        }
        return propertyNames;
    }


    private Property getProperty( final PropertyPath.Element element )
    {
        return getProperty( element.getName(), element.getIndex() );
    }

    public ImmutableList<Property> getProperties( final String name )
    {
        Property.checkName( name );
        final PropertyArray propertyArray = this.propertyArrayByName.get( name );
        if ( propertyArray == null )
        {
            return ImmutableList.of();
        }

        return propertyArray.getProperties();
    }

    public Iterable<Property> getProperties()
    {
        final ImmutableList.Builder<Property> builder = new ImmutableList.Builder<>();
        for ( final PropertyArray propertyArray : this.propertyArrayByName.values() )
        {
            for ( Property property : propertyArray.getProperties() )
            {
                builder.add( property );
            }
        }
        return builder.build();
    }

    Iterable<PropertyArray> getPropertyArrays()
    {
        return this.propertyArrayByName.values();
    }

    PropertyArray getPropertyArray( final String name )
    {
        return this.propertyArrayByName.get( name );
    }

    public Value getValue( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue() : null;
    }

    public Value getValue( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue() : null;
    }

    public Value getValue( final String path )
    {
        return getValue( PropertyPath.from( path ) );
    }

    public Iterable<Value> getValues( final String name )
    {
        final ImmutableList.Builder<Value> valueBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            valueBuilder.add( property.getValue() );
        }
        return valueBuilder.build();
    }

    public PropertySet getPropertySet( final PropertyPath path )
    {
        final Property property = getProperty( path );
        if ( property == null )
        {
            return null;
        }
        return property.getValue().asData();
    }

    public PropertySet getPropertySet( final String path )
    {
        return getPropertySet( PropertyPath.from( path ) );
    }

    // Typed methods for creating a Property

    // setting set
    public Property setSet( final String path, final PropertySet value )
    {
        return this.setSet( PropertyPath.from( path ), value );
    }

    public Property setSet( final PropertyPath path, final PropertySet value )
    {
        return this.setProperty( path, Value.newData( value ) );
    }

    public Property setSet( final String name, final int index, final PropertySet value )
    {
        return this.setProperty( name, index, Value.newData( value ) );
    }

    public Property addSet( final String name, final PropertySet value )
    {
        return this.addProperty( name, Value.newData( value ) );
    }

    public Property[] addSets( final String name, final PropertySet... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newData( values[i] ) );
        }
        return properties;
    }

    /**
     * Creates a new PropertySet attached to the same PropertyTree as this
     * and adds it to this PropertySet with the given name.
     *
     * @return returns the created PropertySet.
     */
    public PropertySet addSet( final String name )
    {
        final PropertySet propertySet = new PropertySet( propertyTree );
        addSet( name, propertySet );
        return propertySet;
    }

    // setting string
    public Property setString( final String path, final String value )
    {
        return this.setString( PropertyPath.from( path ), value );
    }

    public Property setString( final PropertyPath path, final String value )
    {
        return this.setProperty( path, Value.newString( value ) );
    }

    public Property setString( final String name, final int index, final String value )
    {
        return this.setProperty( name, index, Value.newString( value ) );
    }

    public Property addString( final String name, final String value )
    {
        return this.addProperty( name, Value.newString( value ) );
    }

    public Property[] addStrings( final String name, final String... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newString( values[i] ) );
        }
        return properties;
    }
    // setting xml

    public Property setXml( final String path, final String value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newXml( value ) );
    }

    public Property setXml( final PropertyPath path, final String value )
    {
        return this.setProperty( path, Value.newXml( value ) );
    }

    public Property setXml( final String name, final int index, final String value )
    {
        return this.setProperty( name, index, Value.newXml( value ) );
    }

    public Property addXml( final String name, final String value )
    {
        return this.addProperty( name, Value.newXml( value ) );
    }

    public Property[] addXmls( final String name, final String... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newXml( values[i] ) );
        }
        return properties;
    }

    // setting html part

    public Property setHtmlPart( final String path, final String value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newHtmlPart( value ) );
    }

    public Property setHtmlPart( final PropertyPath path, final String value )
    {
        return this.setProperty( path, Value.newHtmlPart( value ) );
    }

    public Property setHtmlPart( final String name, final int index, final String value )
    {
        return this.setProperty( name, index, Value.newHtmlPart( value ) );
    }

    public Property addHtmlPart( final String name, final String value )
    {
        return this.addProperty( name, Value.newHtmlPart( value ) );
    }

    public Property[] addHtmlParts( final String name, final String... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newHtmlPart( values[i] ) );
        }
        return properties;
    }

    // setting boolean

    public Property setBoolean( final String path, final Boolean value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newBoolean( value ) );
    }

    public Property setBoolean( final PropertyPath path, final Boolean value )
    {
        return this.setProperty( path, Value.newBoolean( value ) );
    }

    public Property setBoolean( final String name, final int index, final Boolean value )
    {
        return this.setProperty( name, index, Value.newBoolean( value ) );
    }

    public Property addBoolean( final String name, final Boolean value )
    {
        return this.addProperty( name, Value.newBoolean( value ) );
    }

    public Property[] addBooleans( final String name, final Boolean... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newBoolean( values[i] ) );
        }
        return properties;
    }

    // setting long

    public Property setLong( final String path, final Long value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newLong( value ) );
    }

    public Property setLong( final PropertyPath path, final Long value )
    {
        return this.setProperty( path, Value.newLong( value ) );
    }

    public Property setLong( final String name, final int index, final Long value )
    {
        return this.setProperty( name, index, Value.newLong( value ) );
    }

    public Property addLong( final String name, final Long value )
    {
        return this.addProperty( name, Value.newLong( value ) );
    }

    public Property[] addLongs( final String name, final Long... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newLong( values[i] ) );
        }
        return properties;
    }

    // setting local date

    public Property setLocalDate( final String path, final LocalDate value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newLocalDate( value ) );
    }

    public Property setLocalDate( final PropertyPath path, final LocalDate value )
    {
        return this.setProperty( path, Value.newLocalDate( value ) );
    }

    public Property setLocalDate( final String name, final int index, final LocalDate value )
    {
        return this.setProperty( name, index, Value.newLocalDate( value ) );
    }

    public Property addLocalDate( final String name, final LocalDate value )
    {
        return this.addProperty( name, Value.newLocalDate( value ) );
    }

    public Property[] addLocalDates( final String name, final LocalDate... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newLocalDate( values[i] ) );
        }
        return properties;
    }

    // setting local date time

    public Property setLocalDateTime( final String path, final LocalDateTime value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newLocalDateTime( value ) );
    }

    public Property setLocalDateTime( final PropertyPath path, final LocalDateTime value )
    {
        return this.setProperty( path, Value.newLocalDateTime( value ) );
    }

    public Property setLocalDateTime( final String name, final int index, final LocalDateTime value )
    {
        return this.setProperty( name, index, Value.newLocalDateTime( value ) );
    }

    public Property addLocalDateTime( final String name, final LocalDateTime value )
    {
        return this.addProperty( name, Value.newLocalDateTime( value ) );
    }

    public Property[] addLocalDateTimes( final String name, final LocalDateTime... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newLocalDateTime( values[i] ) );
        }
        return properties;
    }

    // setting local time

    public Property setLocalTime( final String path, final LocalTime value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newLocalTime( value ) );
    }

    public Property setLocalTime( final PropertyPath path, final LocalTime value )
    {
        return this.setProperty( path, Value.newLocalTime( value ) );
    }

    public Property setLocalTime( final String name, final int index, final LocalTime value )
    {
        return this.setProperty( name, index, Value.newLocalTime( value ) );
    }

    public Property addLocalTime( final String name, final LocalTime value )
    {
        return this.addProperty( name, Value.newLocalTime( value ) );
    }

    public Property[] addLocalTimes( final String name, final LocalTime... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newLocalTime( values[i] ) );
        }
        return properties;
    }

    // setting instant

    public Property setInstant( final String path, final Instant value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newInstant( value ) );
    }

    public Property setInstant( final PropertyPath path, final Instant value )
    {
        return this.setProperty( path, Value.newInstant( value ) );
    }

    public Property setInstant( final String name, final int index, final Instant value )
    {
        return this.setProperty( name, index, Value.newInstant( value ) );
    }

    public Property addInstant( final String name, final Instant value )
    {
        return this.addProperty( name, Value.newInstant( value ) );
    }

    public Property[] addInstants( final String name, final Instant... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newInstant( values[i] ) );
        }
        return properties;
    }

    // setting double

    public Property setDouble( final String path, final Double value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newDouble( value ) );
    }

    public Property setDouble( final PropertyPath path, final Double value )
    {
        return this.setProperty( path, Value.newDouble( value ) );
    }

    public Property setDouble( final String name, final int index, final Double value )
    {
        return this.setProperty( name, index, Value.newDouble( value ) );
    }

    public Property addDouble( final String name, final Double value )
    {
        return this.addProperty( name, Value.newDouble( value ) );
    }

    public Property[] addDoubles( final String name, final Double... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newDouble( values[i] ) );
        }
        return properties;
    }

    // setting geo point

    public Property setGeoPoint( final String path, final GeoPoint value )
    {
        return this.setProperty( PropertyPath.from( path ), Value.newGeoPoint( value ) );
    }

    public Property setGeoPoint( final PropertyPath path, final GeoPoint value )
    {
        return this.setProperty( path, Value.newGeoPoint( value ) );
    }

    public Property setGeoPoint( final String name, final int index, final GeoPoint value )
    {
        return this.setProperty( name, index, Value.newGeoPoint( value ) );
    }

    public Property addGeoPoint( final String name, final GeoPoint value )
    {
        return this.addProperty( name, Value.newGeoPoint( value ) );
    }

    public Property[] addGeoPoints( final String name, final GeoPoint... values )
    {
        final Property[] properties = new Property[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            properties[i] = this.addProperty( name, Value.newGeoPoint( values[i] ) );
        }
        return properties;
    }

    // Typed methods for getting Property value

    // getting property set

    public PropertySet getSet( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getSet() : null;
    }

    public PropertySet getSet( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getSet() : null;
    }

    public PropertySet getSet( final String path )
    {
        return getSet( PropertyPath.from( path ) );
    }

    public Iterable<PropertySet> getSets( final String name )
    {
        final ImmutableList.Builder<PropertySet> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getSet() );
        }
        return stringsBuilder.build();
    }

    // getting string

    public String getString( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asString() : null;
    }

    public String getString( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asString() : null;
    }

    public String getString( final String path )
    {
        return getString( PropertyPath.from( path ) );
    }

    public Iterable<String> getStrings( final String name )
    {
        final ImmutableList.Builder<String> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getString() );
        }
        return stringsBuilder.build();
    }

    // getting boolean

    public Boolean getBoolean( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asBoolean() : null;
    }

    public Boolean getBoolean( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asBoolean() : null;
    }

    public Boolean getBoolean( final String path )
    {
        return getBoolean( PropertyPath.from( path ) );
    }

    public Iterable<Boolean> getBooleans( final String name )
    {
        final ImmutableList.Builder<Boolean> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getBoolean() );
        }
        return stringsBuilder.build();
    }

    // getting long

    public Long getLong( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asLong() : null;
    }

    public Long getLong( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asLong() : null;
    }

    public Long getLong( final String path )
    {
        return getLong( PropertyPath.from( path ) );
    }

    public Iterable<Long> getLongs( final String name )
    {
        final ImmutableList.Builder<Long> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getLong() );
        }
        return stringsBuilder.build();
    }

    // getting double

    public Double getDouble( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asDouble() : null;
    }

    public Double getDouble( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asDouble() : null;
    }

    public Double getDouble( final String path )
    {
        return getDouble( PropertyPath.from( path ) );
    }

    public Iterable<Double> getDoubles( final String name )
    {
        final ImmutableList.Builder<Double> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getDouble() );
        }
        return stringsBuilder.build();
    }

    // getting geo point

    public GeoPoint getGeoPoint( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asGeoPoint() : null;
    }

    public GeoPoint getGeoPoint( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asGeoPoint() : null;
    }

    public GeoPoint getGeoPoint( final String path )
    {
        return getGeoPoint( PropertyPath.from( path ) );
    }

    public Iterable<GeoPoint> getGeoPoints( final String name )
    {
        final ImmutableList.Builder<GeoPoint> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getGeoPoint() );
        }
        return stringsBuilder.build();
    }

    // getting local date

    public LocalDate getLocalDate( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getValue().asLocalDate() : null;
    }

    public LocalDate getLocalDate( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getValue().asLocalDate() : null;
    }

    public LocalDate getLocalDate( final String path )
    {
        return getLocalDate( PropertyPath.from( path ) );
    }

    public Iterable<LocalDate> getLocalDates( final String name )
    {
        final ImmutableList.Builder<LocalDate> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getLocalDate() );
        }
        return stringsBuilder.build();
    }

    // getting local date time

    public LocalDateTime getLocalDateTime( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getLocalDateTime() : null;
    }

    public LocalDateTime getLocalDateTime( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getLocalDateTime() : null;
    }

    public LocalDateTime getLocalDateTime( final String path )
    {
        return getLocalDateTime( PropertyPath.from( path ) );
    }

    public Iterable<LocalDateTime> getLocalDateTimes( final String name )
    {
        final ImmutableList.Builder<LocalDateTime> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getLocalDateTime() );
        }
        return stringsBuilder.build();
    }

    // getting local time

    public LocalTime getLocalTime( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getLocalTime() : null;
    }

    public LocalTime getLocalTime( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getLocalTime() : null;
    }

    public LocalTime getLocalTime( final String path )
    {
        return getLocalTime( PropertyPath.from( path ) );
    }

    public Iterable<LocalTime> getLocalTimes( final String name )
    {
        final ImmutableList.Builder<LocalTime> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getLocalTime() );
        }
        return stringsBuilder.build();
    }

    // getting instant

    public Instant getInstant( final String name, final int index )
    {
        final Property property = this.getProperty( name, index );
        return property != null ? property.getInstant() : null;
    }

    public Instant getInstant( final PropertyPath path )
    {
        final Property property = this.getProperty( path );
        return property != null ? property.getInstant() : null;
    }

    public Instant getInstant( final String path )
    {
        return getInstant( PropertyPath.from( path ) );
    }

    public Iterable<Instant> getInstants( final String name )
    {
        final ImmutableList.Builder<Instant> stringsBuilder = new ImmutableList.Builder<>();
        for ( final Property property : getProperties( name ) )
        {
            stringsBuilder.add( property.getInstant() );
        }
        return stringsBuilder.build();
    }
}