package com.hendrik.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

import com.hendrik.http.http.Request;
import com.hendrik.http.http.Response;
import com.hendrik.http.http.ResponseBuilder;
import com.hendrik.http.http.resource.Resource;
import com.hendrik.http.http.HeaderFields;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ETagTest {

    @Test
    public void testEtagInResponse() {

        try {
            Request getRootRequest = new Request(new ByteArrayInputStream("GET / HTTP/1.1".getBytes()));

            Response getRootResponse = new ResponseBuilder(getRootRequest).setEtag().build();
            Assertions.assertTrue(getRootResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testIfMatch() {

        try {
            Request getRootRequestIfMatch = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: 533839800".getBytes()));
            Request getRootRequestIfMatchStar = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request getRootRequestIfMatchStarResourceNotExisting = new Request(new ByteArrayInputStream("GET /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request getRootRequestIfMatchNotMatching = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: 533839801".getBytes()));

            Resource rootHTML = Resource.createFromPath("/Test1/root.html");

            Response getRootResponseIfMatch = new ResponseBuilder(getRootRequestIfMatch).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());

            Assertions.assertArrayEquals(rootHTML.getData(), getRootResponseIfMatch.getData());

            Response getRootResponseIfMatchStar = new ResponseBuilder(getRootRequestIfMatchStar).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());

            Assertions.assertArrayEquals(rootHTML.getData(), getRootResponseIfMatchStar.getData());

            Response getRootResponseIfMatchNotMatching = new ResponseBuilder(getRootRequestIfMatchNotMatching).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("413 Precondition Failed".getBytes(), getRootResponseIfMatchNotMatching.getData());

            Response getRootResponseIfMatchingStarNotExisting = new ResponseBuilder(getRootRequestIfMatchStarResourceNotExisting).setEtag().build();
            Assertions.assertFalse(getRootResponseIfMatchingStarNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("404 Not Found".getBytes(), getRootResponseIfMatchingStarNotExisting.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testIfModifiedSince() {
        try {
            String dateNow = DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetTime.now());
            Request getRootRequestIfModifiedSinceNow = new Request(new ByteArrayInputStream(("GET / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateNow).getBytes()));

            Response getRootResponseIfModifiedSinceNow = new ResponseBuilder(getRootRequestIfModifiedSinceNow).setEtag().build();
            Assertions.assertFalse(getRootResponseIfModifiedSinceNow.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("404 Not Found".getBytes(), getRootResponseIfModifiedSinceNow.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }
    }

}
