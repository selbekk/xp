package com.enonic.xp.content.page.region;


import org.junit.Test;

import com.enonic.xp.content.page.region.ComponentPath;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ComponentPathTest
{
    @Test(expected = IllegalArgumentException.class)
    public void from_throws_IllegalArgumentException_when_odd_number_of_path_elements()
    {
        ComponentPath.from( "region[0]" );
    }

    @Test
    public void tostring()
    {
        assertEquals( "my-region/0", ComponentPath.from( "my-region/0" ).toString() );
        assertEquals( "my-region/1", ComponentPath.from( "my-region/1" ).toString() );
        assertEquals( "my-other-region/0/my-region/1", ComponentPath.from( "my-other-region/0/my-region/1" ).toString() );
    }

    @Test
    public void removeFirstLevel()
    {
        assertEquals( "my-region/1", ComponentPath.from( "my-other-region/0/my-region/1" ).removeFirstLevel().toString() );
        assertNull( ComponentPath.from( "my-region/0" ).removeFirstLevel() );
    }
}