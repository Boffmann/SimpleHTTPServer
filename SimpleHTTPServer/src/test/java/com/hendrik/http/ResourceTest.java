package com.hendrik.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.hendrik.http.resource.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResourceTest {

    private static Resource rootResource;
    private static Resource notRootResource;
    private static Resource notRootInSubfolder;
    private static Resource notExistingNotRootResource;
    private static Resource umlauteNotExisting;
    private static Resource umlauteExisting;
    private static Resource fileWithSpaces;

    private static Resource rootHTML;

    @BeforeAll
    public static void setUp() {

        try {
            rootResource = Resource.createFromURI("/");
            notRootResource = Resource.createFromURI("/Test1");
            notRootInSubfolder = Resource.createFromURI("/Test2/Test21/subfolder.txt");
            notExistingNotRootResource = Resource.createFromURI("/afeafeda/faef");
            umlauteNotExisting = Resource.createFromURI("/Döcu_ments");
            umlauteExisting = Resource.createFromURI("/Test1/umläute.txt");
            fileWithSpaces = Resource.createFromURI("/Test2/File With Spaces.txt");

            rootHTML = Resource.createFromURI("/Test1/root.html");
        } catch (UnsupportedEncodingException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }
    }


    @Test
    public void testIsRoot() {
        Assertions.assertTrue(rootResource.isRoot());
        Assertions.assertFalse(notRootResource.isRoot());
        Assertions.assertFalse(notRootInSubfolder.isRoot());
        Assertions.assertFalse(notExistingNotRootResource.isRoot());
        Assertions.assertFalse(umlauteNotExisting.isRoot());
        Assertions.assertFalse(fileWithSpaces.isRoot());
    }

    @Test
    public void testExists() {
        Assertions.assertTrue(rootResource.exists());
        Assertions.assertTrue(notRootResource.exists());
        Assertions.assertTrue(notRootInSubfolder.exists());
        Assertions.assertFalse(notExistingNotRootResource.exists());
        Assertions.assertTrue(umlauteExisting.exists());
        Assertions.assertTrue(rootHTML.exists());
        Assertions.assertTrue(fileWithSpaces.exists());
    }

    @Test
    public void testGetName() {
        Assertions.assertEquals("Test", rootResource.getName());
        Assertions.assertEquals("Test1", notRootResource.getName());
        Assertions.assertEquals("subfolder.txt", notRootInSubfolder.getName());
        Assertions.assertEquals(null, notExistingNotRootResource.getName());
        Assertions.assertEquals("umläute.txt", umlauteExisting.getName());
    }

    @Test
    public void testGetPath() {
        Assertions.assertEquals("/", rootResource.getPath());
        Assertions.assertEquals("/Test1", notRootResource.getPath());
        Assertions.assertEquals("/Test2/Test21/subfolder.txt", notRootInSubfolder.getPath());
        Assertions.assertEquals(null, notExistingNotRootResource.getPath());
        Assertions.assertEquals("/Test1/umläute.txt", umlauteExisting.getPath());
    }

    @Test
    public void testGetParentDir() {
        Assertions.assertEquals("/", rootResource.getParentDirectory());
        Assertions.assertEquals("/", notRootResource.getParentDirectory());
        Assertions.assertEquals("/Test2/Test21", notRootInSubfolder.getParentDirectory());
        Assertions.assertEquals(null, notExistingNotRootResource.getParentDirectory());
        Assertions.assertEquals("/Test1", umlauteExisting.getParentDirectory());
    }

    @Test
    public void testGetData() {

        try {
            Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
            Assertions.assertArrayEquals("Subfolder\n".getBytes(), fileInSubfolders.getData());

            Resource rootHTML = Resource.createFromURI("/Test1/root.html");
            Resource rootResource = Resource.createFromURI("/");
            Assertions.assertArrayEquals(rootHTML.getData(), rootResource.getData());

            Resource notExisting = Resource.createFromURI("/afeafeda/faef");
            Assertions.assertArrayEquals("".getBytes(), notExisting.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testGetContentType() {
        Assertions.assertEquals("text/html; charset=utf-8", rootResource.getContentType());
        Assertions.assertEquals("text/html; charset=utf-8", notRootResource.getContentType());
        Assertions.assertEquals("text/plain; charset=utf-8", notRootInSubfolder.getContentType());
        Assertions.assertEquals(null, notExistingNotRootResource.getContentType());
        Assertions.assertEquals("text/plain; charset=utf-8", umlauteExisting.getContentType());
        Assertions.assertEquals("text/html; charset=utf-8", rootHTML.getContentType());
    }
}
