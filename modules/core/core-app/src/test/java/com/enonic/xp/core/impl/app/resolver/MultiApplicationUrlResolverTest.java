package com.enonic.xp.core.impl.app.resolver;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;

public class MultiApplicationUrlResolverTest
{
    private ApplicationUrlResolver delegate1;

    private ApplicationUrlResolver delegate2;

    private MultiApplicationUrlResolver resolver;

    @Before
    public void setup()
    {
        this.delegate1 = Mockito.mock( ApplicationUrlResolver.class );
        this.delegate2 = Mockito.mock( ApplicationUrlResolver.class );

        this.resolver = new MultiApplicationUrlResolver( this.delegate1, this.delegate2 );
    }

    @Test
    public void testFindFiles()
    {
        Mockito.when( this.delegate1.findFiles() ).thenReturn( Sets.newHashSet( "a/b/c.txt", "a/other.txt" ) );
        Mockito.when( this.delegate2.findFiles() ).thenReturn( Sets.newHashSet( "a/other.txt", "a/b/other.txt" ) );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 3, files.size() );

        assertTrue( files.contains( "a/b/c.txt" ) );
        assertTrue( files.contains( "a/other.txt" ) );
        assertTrue( files.contains( "a/b/other.txt" ) );
    }

    @Test
    public void testFindUrl()
        throws Exception
    {
        final URL expected1 = new File( "." ).toURI().toURL();
        Mockito.when( this.delegate1.findUrl( "a/b.txt" ) ).thenReturn( expected1 );

        final URL expected2 = new File( "." ).toURI().toURL();
        Mockito.when( this.delegate2.findUrl( "a/other.txt" ) ).thenReturn( expected2 );

        final URL url1 = this.resolver.findUrl( "a/b.txt" );
        assertSame( expected1, url1 );

        final URL url2 = this.resolver.findUrl( "a/other.txt" );
        assertSame( expected2, url2 );

        final URL url3 = this.resolver.findUrl( "other.txt" );
        assertNull( url3 );
    }
}
