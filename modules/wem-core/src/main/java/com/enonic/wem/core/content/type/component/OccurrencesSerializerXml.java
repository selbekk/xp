package com.enonic.wem.core.content.type.component;


import org.jdom.Attribute;
import org.jdom.Element;

import com.enonic.wem.api.content.type.component.Occurrences;

class OccurrencesSerializerXml
{
    public Element serialize( final Occurrences occurrences )
    {
        Element occurrencesEl = new Element( "occurrences" );
        if ( null != occurrences )
        {
            occurrencesEl.setAttribute( "minimum", String.valueOf( occurrences.getMinimum() ) );
            occurrencesEl.setAttribute( "maximum", String.valueOf( occurrences.getMaximum() ) );
        }
        return occurrencesEl;
    }

    public Occurrences parse( final Element parentEl )
    {
        Element occurrencesEl = parentEl.getChild( "occurrences" );
        if ( occurrencesEl == null )
        {
            return null;
        }

        int minimum = 0;
        int maximum = 0;

        Attribute minimumAttr = occurrencesEl.getAttribute( "minimum" );
        Attribute maximumAttr = occurrencesEl.getAttribute( "maximum" );

        if ( minimumAttr != null )
        {
            minimum = Integer.parseInt( minimumAttr.getValue() );
        }

        if ( maximumAttr != null )
        {
            maximum = Integer.parseInt( maximumAttr.getValue() );
        }

        if ( maximumAttr == null && minimumAttr == null )
        {
            return null;
        }

        return new Occurrences( minimum, maximum );
    }
}
