package com.hendrik.http;

import java.io.File;
import java.io.IOException;

import javax.management.InvalidAttributeValueException;

import com.hendrik.http.http.resource.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResourceTest {

    /**
     * This HTTP Server is only used to set the root directory
     */
    private static HTTPServer server;

    /**
     * Path into the test directory
     */
    private static String testDirPath;

    /**
     * Sets up a new HTTP server with root directory in test folder.
     * This should not start the server, though
     */
    @BeforeAll
    public static void setUp() {

        File workingDir = new File(System.getProperty("user.dir"));
        File testDir = new File(workingDir.getParent() + "/Test");
        testDirPath = testDir.getAbsolutePath();

        try {
            server = new HTTPServer(8080, testDirPath);
        } catch (InvalidAttributeValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testIsRoot() {
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertTrue(rootResource.isRoot());
        Resource notRootResource = Resource.createFromURI("/Test1");
        Assertions.assertFalse(notRootResource.isRoot());
        Resource notRootInSubfolder = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertFalse(notRootInSubfolder.isRoot());
        Resource notExistingNotRootResource = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertFalse(notExistingNotRootResource.isRoot());
        Resource umlaute = Resource.createFromURI("/Döcu_ments");
        Assertions.assertFalse(umlaute.isRoot());
    }

    @Test
    public void testExists() {
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertTrue(rootResource.exists());
        Resource existing = Resource.createFromURI("/Test1");
        Assertions.assertTrue(existing.exists());
        Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertTrue(fileInSubfolders.exists());
        Resource notExisting = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertFalse(notExisting.exists());
        Resource umlaute = Resource.createFromURI("/Test1/umläute.txt");
        Assertions.assertTrue(umlaute.exists());
        Resource rootHTML = Resource.createFromURI("/Test1/root.html");
        Assertions.assertTrue(rootHTML.exists());
    }

    @Test
    public void testGetName() {
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertEquals("Test", rootResource.getName());
        Resource existing = Resource.createFromURI("/Test1");
        Assertions.assertEquals("Test1", existing.getName());
        Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("subfolder.txt", fileInSubfolders.getName());
        Resource notExisting = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getName());
        Resource umlaute = Resource.createFromURI("/Test1/umläute.txt");
        Assertions.assertEquals("umläute.txt", umlaute.getName());
    }

    @Test
    public void testGetPath() {
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertEquals("/", rootResource.getPath());
        Resource existing = Resource.createFromURI("/Test1");
        Assertions.assertEquals("/Test1", existing.getPath());
        Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("/Test2/Test21/subfolder.txt", fileInSubfolders.getPath());
        Resource notExisting = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getPath());
        Resource umlaute = Resource.createFromURI("/Test1/umläute.txt");
        Assertions.assertEquals("/Test1/umläute.txt", umlaute.getPath());
    }

    @Test
    public void testGetParentDir() {
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertEquals("/", rootResource.getParentDirectory());
        Resource existing = Resource.createFromURI("/Test1");
        Assertions.assertEquals("/", existing.getParentDirectory());
        Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("/Test2/Test21", fileInSubfolders.getParentDirectory());
        Resource notExisting = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getParentDirectory());
        Resource umlaute = Resource.createFromURI("/Test1/umläute.txt");
        Assertions.assertEquals("/Test1", umlaute.getParentDirectory());
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
        Resource rootResource = Resource.createFromURI("/");
        Assertions.assertEquals("text/html", rootResource.getContentType());
        Resource existing = Resource.createFromURI("/Test1");
        Assertions.assertEquals("text/html", existing.getContentType());
        Resource fileInSubfolders = Resource.createFromURI("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("text/plain", fileInSubfolders.getContentType());
        Resource notExisting = Resource.createFromURI("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getContentType());
        Resource umlaute = Resource.createFromURI("/Test1/umläute.txt");
        Assertions.assertEquals("text/plain", umlaute.getContentType());
        Resource rootHTML = Resource.createFromURI("/Test1/root.html");
        Assertions.assertEquals("text/html", rootHTML.getContentType());
    }
}
