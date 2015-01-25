package com.enonic.wem.api.schema.content;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleBasedName;
import com.enonic.wem.api.module.ModuleKey;

public final class ContentTypeName
    extends ModuleBasedName
    implements Comparable<ContentTypeName>
{
    private static final ContentTypeName UNSTRUCTURED = new ContentTypeName( "unstructured" );

    private static final ContentTypeName STRUCTURED = new ContentTypeName( "structured" );

    private static final ContentTypeName FOLDER = new ContentTypeName( "folder" );

    private static final ContentTypeName PAGE_TEMPLATE = new ContentTypeName( "page-template" );

    private static final ContentTypeName TEMPLATE_FOLDER = new ContentTypeName( "template-folder" );

    private static final ContentTypeName SITE = new ContentTypeName( "site" );

    private static final ContentTypeName SHORTCUT = new ContentTypeName( "shortcut" );

    private static final ContentTypeName MEDIA = new ContentTypeName( "media" );

    private static final ContentTypeName MEDIA_TEXT = new ContentTypeName( "text" );

    private static final ContentTypeName MEDIA_DATA = new ContentTypeName( "data" );

    private static final ContentTypeName MEDIA_AUDIO = new ContentTypeName( "audio" );

    private static final ContentTypeName MEDIA_VIDEO = new ContentTypeName( "video" );

    private static final ContentTypeName MEDIA_IMAGE = new ContentTypeName( "image" );

    private static final ContentTypeName MEDIA_VECTOR = new ContentTypeName( "vector" );

    private static final ContentTypeName MEDIA_ARCHIVE = new ContentTypeName( "archive" );

    private static final ContentTypeName MEDIA_DOCUMENT = new ContentTypeName( "document" );

    private static final ContentTypeName MEDIA_SPREADSHEET = new ContentTypeName( "spreadsheet" );

    private static final ContentTypeName MEDIA_PRESENTATION = new ContentTypeName( "presentation" );

    private static final ContentTypeName MEDIA_CODE = new ContentTypeName( "code" );

    private static final ContentTypeName MEDIA_EXECUTABLE = new ContentTypeName( "executable" );

    private static final ContentTypeName MEDIA_UNKNOWN = new ContentTypeName( "unknown" );

    private ContentTypeName( final String localName )
    {
        super( ModuleKey.SYSTEM, localName );
    }

    private ContentTypeName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    public static ContentTypeName structured()
    {
        return STRUCTURED;
    }

    public static ContentTypeName unstructured()
    {
        return UNSTRUCTURED;
    }

    public static ContentTypeName folder()
    {
        return FOLDER;
    }

    public static ContentTypeName pageTemplate()
    {
        return PAGE_TEMPLATE;
    }

    public static ContentTypeName templateFolder()
    {
        return TEMPLATE_FOLDER;
    }

    public static ContentTypeName site()
    {
        return SITE;
    }

    public static ContentTypeName shortcut()
    {
        return SHORTCUT;
    }

    public static ContentTypeName media()
    {
        return MEDIA;
    }

    public static ContentTypeName textMedia()
    {
        return MEDIA_TEXT;
    }

    public static ContentTypeName dataMedia()
    {
        return MEDIA_DATA;
    }

    public static ContentTypeName audioMedia()
    {
        return MEDIA_AUDIO;
    }

    public static ContentTypeName videoMedia()
    {
        return MEDIA_VIDEO;
    }

    public static ContentTypeName imageMedia()
    {
        return MEDIA_IMAGE;
    }

    public static ContentTypeName vectorMedia()
    {
        return MEDIA_VECTOR;
    }

    public static ContentTypeName archiveMedia()
    {
        return MEDIA_ARCHIVE;
    }

    public static ContentTypeName documentMedia()
    {
        return MEDIA_DOCUMENT;
    }

    public static ContentTypeName spreadsheetMedia()
    {
        return MEDIA_SPREADSHEET;
    }

    public static ContentTypeName presentationMedia()
    {
        return MEDIA_PRESENTATION;
    }

    public static ContentTypeName codeMedia()
    {
        return MEDIA_CODE;
    }

    public static ContentTypeName unknownMedia()
    {
        return MEDIA_UNKNOWN;
    }

    public static ContentTypeName executableMedia()
    {
        return MEDIA_EXECUTABLE;
    }

    public boolean isUnstructured()
    {
        return UNSTRUCTURED.equals( this );
    }

    public boolean isStructured()
    {
        return STRUCTURED.equals( this );
    }

    public boolean isFolder()
    {
        return FOLDER.equals( this );
    }

    public boolean isTemplateFolder()
    {
        return TEMPLATE_FOLDER.equals( this );
    }

    public boolean isPageTemplate()
    {
        return PAGE_TEMPLATE.equals( this );
    }

    public boolean isSite()
    {
        return SITE.equals( this );
    }

    public boolean isShortcut()
    {
        return SHORTCUT.equals( this );
    }

    public boolean isMedia()
    {
        return MEDIA.equals( this );
    }

    public boolean isDescendantOfMedia()
    {
        return MEDIA_ARCHIVE.equals( this ) ||
            MEDIA_AUDIO.equals( this ) ||
            MEDIA_VIDEO.equals( this ) ||
            MEDIA_CODE.equals( this ) ||
            MEDIA_DATA.equals( this ) ||
            MEDIA_DOCUMENT.equals( this ) ||
            MEDIA_EXECUTABLE.equals( this ) ||
            MEDIA_IMAGE.equals( this ) ||
            MEDIA_SPREADSHEET.equals( this ) ||
            MEDIA_PRESENTATION.equals( this ) ||
            MEDIA_VECTOR.equals( this ) ||
            MEDIA_TEXT.equals( this ) ||
            MEDIA_UNKNOWN.equals( this );
    }

    public boolean isTextMedia()
    {
        return MEDIA_TEXT.equals( this );
    }

    public boolean isDataMedia()
    {
        return MEDIA_DATA.equals( this );
    }

    public boolean isAudioMedia()
    {
        return MEDIA_AUDIO.equals( this );
    }

    public boolean isVideoMedia()
    {
        return MEDIA_VIDEO.equals( this );
    }

    public boolean isImageMedia()
    {
        return MEDIA_IMAGE.equals( this );
    }

    public boolean isVectorMedia()
    {
        return MEDIA_VECTOR.equals( this );
    }

    public boolean isArchiveMedia()
    {
        return MEDIA_ARCHIVE.equals( this );
    }

    public boolean isDocumentMedia()
    {
        return MEDIA_DOCUMENT.equals( this );
    }

    public boolean isSpreadsheetMedia()
    {
        return MEDIA_SPREADSHEET.equals( this );
    }

    public boolean isPresentationMedia()
    {
        return MEDIA_PRESENTATION.equals( this );
    }

    public boolean isCodeMedia()
    {
        return MEDIA_CODE.equals( this );
    }

    public boolean isExecutableMedia()
    {
        return MEDIA_EXECUTABLE.equals( this );
    }

    public boolean isUnknownMedia()
    {
        return MEDIA_UNKNOWN.equals( this );
    }

    @Override
    public int compareTo( final ContentTypeName that )
    {
        return this.toString().compareTo( that.toString() );
    }

    public static ContentTypeName from( final ModuleKey moduleKey, final String localName )
    {
        return new ContentTypeName( moduleKey, localName );
    }

    public static ContentTypeName from( final String contentTypeName )
    {
        final String moduleKey = StringUtils.substringBefore( contentTypeName, SEPARATOR );
        final String localName = StringUtils.substringAfter( contentTypeName, SEPARATOR );

        return new ContentTypeName( ModuleKey.from( moduleKey ), localName );
    }
}