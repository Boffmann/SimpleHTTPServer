package com.hendrik.http;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertTrue(rootResource.isRoot());
        Resource notRootResource = Resource.createFromPath("/Test1");
        Assertions.assertFalse(notRootResource.isRoot());
        Resource notRootInSubfolder = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertFalse(notRootInSubfolder.isRoot());
        Resource notExistingNotRootResource = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertFalse(notExistingNotRootResource.isRoot());
        Resource umlaute = Resource.createFromPath("/Döcu_ments");
        Assertions.assertFalse(umlaute.isRoot());
    }

    @Test
    public void testExists() {
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertTrue(rootResource.exists());
        Resource existing = Resource.createFromPath("/Test1");
        Assertions.assertTrue(existing.exists());
        Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertTrue(fileInSubfolders.exists());
        Resource notExisting = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertFalse(notExisting.exists());
        Resource umlaute = Resource.createFromPath("/Test1/umläute.txt");
        Assertions.assertTrue(umlaute.exists());
        Resource rootHTML = Resource.createFromPath("/Test1/root.html");
        Assertions.assertTrue(rootHTML.exists());
    }

    @Test
    public void testGetName() {
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertEquals("Test", rootResource.getName());
        Resource existing = Resource.createFromPath("/Test1");
        Assertions.assertEquals("Test1", existing.getName());
        Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("subfolder.txt", fileInSubfolders.getName());
        Resource notExisting = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getName());
        Resource umlaute = Resource.createFromPath("/Test1/umläute.txt");
        Assertions.assertEquals("umläute.txt", umlaute.getName());
    }

    @Test
    public void testGetPath() {
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertEquals("/", rootResource.getPath());
        Resource existing = Resource.createFromPath("/Test1");
        Assertions.assertEquals("/Test1", existing.getPath());
        Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("/Test2/Test21/subfolder.txt", fileInSubfolders.getPath());
        Resource notExisting = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getPath());
        Resource umlaute = Resource.createFromPath("/Test1/umläute.txt");
        Assertions.assertEquals("/Test1/umläute.txt", umlaute.getPath());
    }

    @Test
    public void testGetParentDir() {
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertEquals("/", rootResource.getParentDirectory());
        Resource existing = Resource.createFromPath("/Test1");
        Assertions.assertEquals("/", existing.getParentDirectory());
        Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("/Test2/Test21", fileInSubfolders.getParentDirectory());
        Resource notExisting = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getParentDirectory());
        Resource umlaute = Resource.createFromPath("/Test1/umläute.txt");
        Assertions.assertEquals("/Test1", umlaute.getParentDirectory());
    }

    @Test
    public void testGetData() {

        try {
            Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
            Assertions.assertArrayEquals("Subfolder\n".getBytes(), fileInSubfolders.getData());

            Resource rootHTML = Resource.createFromPath("/Test1/root.html");
            Resource rootResource = Resource.createFromPath("/");
            Assertions.assertArrayEquals(rootHTML.getData(), rootResource.getData());

            Resource notExisting = Resource.createFromPath("/afeafeda/faef");
            Assertions.assertArrayEquals("404 - File not Found".getBytes(), notExisting.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testGetContentType() {
        Resource rootResource = Resource.createFromPath("/");
        Assertions.assertEquals("text/html", rootResource.getContentType());
        Resource existing = Resource.createFromPath("/Test1");
        Assertions.assertEquals("text/html", existing.getContentType());
        Resource fileInSubfolders = Resource.createFromPath("/Test2/Test21/subfolder.txt");
        Assertions.assertEquals("text/plain", fileInSubfolders.getContentType());
        Resource notExisting = Resource.createFromPath("/afeafeda/faef");
        Assertions.assertEquals(null, notExisting.getContentType());
        Resource umlaute = Resource.createFromPath("/Test1/umläute.txt");
        Assertions.assertEquals("text/plain", umlaute.getContentType());
        Resource rootHTML = Resource.createFromPath("/Test1/root.html");
        Assertions.assertEquals("text/html", rootHTML.getContentType());
    }
}
