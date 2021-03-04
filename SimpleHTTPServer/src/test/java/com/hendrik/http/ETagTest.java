package com.hendrik.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
            Request getRootRequestIfMatch = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: 533839805, 533839800".getBytes()));
            Request getRootRequestIfMatchStar = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request getRootRequestIfMatchStarResourceNotExisting = new Request(new ByteArrayInputStream("GET /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request getRootRequestIfMatchNotMatching = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-Match: 533839801".getBytes()));

            Request headRootRequestIfMatch = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-Match: 533839805, 533839800".getBytes()));
            Request headRootRequestIfMatchStar = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request headRootRequestIfMatchStarResourceNotExisting = new Request(new ByteArrayInputStream("HEAD /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-Match: *".getBytes()));
            Request headRootRequestIfMatchNotMatching = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-Match: 533839801".getBytes()));

            Resource rootHTML = Resource.createFromURI("/Test1/root.html");

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

            Response headRootResponseIfMatch = new ResponseBuilder(headRootRequestIfMatch).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatch.getData());

            Response headRootResponseIfMatchStar = new ResponseBuilder(headRootRequestIfMatchStar).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchStar.getData());

            Response headRootResponseIfMatchNotMatching = new ResponseBuilder(headRootRequestIfMatchNotMatching).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchNotMatching.getData());

            Response headRootResponseIfMatchingStarNotExisting = new ResponseBuilder(headRootRequestIfMatchStarResourceNotExisting).setEtag().build();
            Assertions.assertFalse(headRootResponseIfMatchingStarNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchingStarNotExisting.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }
    
    @Test
    public void testIfNoneMatch() {

        try {
            Request getRootRequestIfMatch = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-None-Match: 533839800".getBytes()));
            Request getRootRequestIfMatchStar = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-None-Match: *".getBytes()));
            Request getRootRequestIfMatchStarResourceNotExisting = new Request(new ByteArrayInputStream("GET /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-None-Match: *".getBytes()));
            Request getRootRequestIfMatchNotMatching = new Request(new ByteArrayInputStream("GET / HTTP/1.1\nETag: 533839800\nIf-None-Match: 533839801".getBytes()));

            Request headRootRequestIfMatch = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-None-Match: 533839800".getBytes()));
            Request headRootRequestIfMatchStar = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-None-Match: *".getBytes()));
            Request headRootRequestIfMatchStarResourceNotExisting = new Request(new ByteArrayInputStream("HEAD /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-None-Match: *".getBytes()));
            Request headRootRequestIfMatchNotMatching = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1\nETag: 533839800\nIf-None-Match: 533839801".getBytes()));


            Resource rootHTML = Resource.createFromURI("/Test1/root.html");

            Response getRootResponseIfMatch = new ResponseBuilder(getRootRequestIfMatch).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("304 Not Modified".getBytes(), getRootResponseIfMatch.getData());

            Response getRootResponseIfMatchStar = new ResponseBuilder(getRootRequestIfMatchStar).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("304 Not Modified".getBytes(), getRootResponseIfMatchStar.getData());

            Response getRootResponseIfMatchNotMatching = new ResponseBuilder(getRootRequestIfMatchNotMatching).setEtag().build();
            Assertions.assertTrue(getRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals(rootHTML.getData(), getRootResponseIfMatchNotMatching.getData());
            Assertions.assertFalse(Arrays.equals("304 Not Modified".getBytes(), getRootResponseIfMatchNotMatching.getData()));

            Response getRootResponseIfMatchingStarNotExisting = new ResponseBuilder(getRootRequestIfMatchStarResourceNotExisting).setEtag().build();
            Assertions.assertFalse(getRootResponseIfMatchingStarNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("404 Not Found".getBytes(), getRootResponseIfMatchingStarNotExisting.getData());

            Response headRootResponseIfMatch = new ResponseBuilder(headRootRequestIfMatch).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatch.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatch.getData());

            Response headRootResponseIfMatchStar = new ResponseBuilder(headRootRequestIfMatchStar).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatchStar.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchStar.getData());

            Response headRootResponseIfMatchNotMatching = new ResponseBuilder(headRootRequestIfMatchNotMatching).setEtag().build();
            Assertions.assertTrue(headRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseIfMatchNotMatching.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchNotMatching.getData());

            Response headRootResponseIfMatchingStarNotExisting = new ResponseBuilder(headRootRequestIfMatchStarResourceNotExisting).setEtag().build();
            Assertions.assertFalse(headRootResponseIfMatchingStarNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfMatchingStarNotExisting.getData());
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }

    }

    @Test
    public void testIfModifiedSince() {
        try {
            Resource rootHTML = Resource.createFromURI("/Test1/root.html");
            System.out.println(HTTPServer.getRootDirectory());

            String dateNow = DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now());
            String dateTomorrow = DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now().plusDays(1));
            String dateBeginning = DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.of(1970, 1, 1, 1, 1, 1, 1, ZoneOffset.ofHours(0)));
            Request getRootRequestIfModifiedSinceNow = new Request(new ByteArrayInputStream(("GET / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateNow).getBytes()));
            Request getRootRequestIfModifiedSinceBeginningOfTime = new Request(new ByteArrayInputStream(("GET / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateBeginning).getBytes()));
            Request getRootRequestResourceNotExisting = new Request(new ByteArrayInputStream(("GET /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateBeginning).getBytes()));
            Request getRootRequestDateInvalid = new Request(new ByteArrayInputStream(("GET / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateTomorrow).getBytes()));
            Request getIfNotMatchAndIfModifiedSince = new Request(new ByteArrayInputStream(("GET / HTTP/1.1\nETag: 533839800\nIf-Modified-Since:  "+ dateNow +"\nIf-None-Match: 533839801").getBytes()));

            Request headRootRequestIfModifiedSinceNow = new Request(new ByteArrayInputStream(("HEAD / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateNow).getBytes()));
            Request headRootRequestIfModifiedSinceBeginningOfTime = new Request(new ByteArrayInputStream(("HEAD / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateBeginning).getBytes()));
            Request headRootRequestResourceNotExisting = new Request(new ByteArrayInputStream(("HEAD /kaudawelsch HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateBeginning).getBytes()));
            Request headRootRequestDateInvalid = new Request(new ByteArrayInputStream(("HEAD / HTTP/1.1\nETag: 533839800\nIf-Modified-Since: "+dateTomorrow).getBytes()));
            Request headIfNotMatchAndIfModifiedSince = new Request(new ByteArrayInputStream(("HEAD / HTTP/1.1\nETag: 533839800\nIf-Modified-Since:  "+ dateNow +"\nIf-None-Match: 533839801").getBytes()));

            Response getRootResponseIfModifiedSinceNow = new ResponseBuilder(getRootRequestIfModifiedSinceNow).setEtag().build();
            Assertions.assertTrue(getRootResponseIfModifiedSinceNow.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootRequestIfModifiedSinceNow.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("304 Not Modified".getBytes(), getRootResponseIfModifiedSinceNow.getData());

            Response getRootResponseIfModifiedSinceBeginningOfTime = new ResponseBuilder(getRootRequestIfModifiedSinceBeginningOfTime).setEtag().build();
            Assertions.assertTrue(getRootResponseIfModifiedSinceBeginningOfTime.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootRequestIfModifiedSinceBeginningOfTime.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals(rootHTML.getData(), getRootResponseIfModifiedSinceBeginningOfTime.getData());

            Response getRootResponseResourceNotExisting = new ResponseBuilder(getRootRequestResourceNotExisting).setEtag().build();
            Assertions.assertFalse(getRootResponseResourceNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("404 Not Found".getBytes(), getRootResponseResourceNotExisting.getData());

            Response getRootResponseDateInvalid = new ResponseBuilder(getRootRequestDateInvalid).setEtag().build();
            Assertions.assertTrue(getRootResponseDateInvalid.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getRootResponseDateInvalid.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals(rootHTML.getData(), getRootResponseDateInvalid.getData());

            Response getIfNotMatchAndModifiedSinceResponse = new ResponseBuilder(getIfNotMatchAndIfModifiedSince).setEtag().build();
            Assertions.assertFalse(Arrays.equals("304 Not Modified".getBytes(), getIfNotMatchAndModifiedSinceResponse.getData()));
            Assertions.assertTrue(getIfNotMatchAndModifiedSinceResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", getIfNotMatchAndModifiedSinceResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals(rootHTML.getData(), getIfNotMatchAndModifiedSinceResponse.getData());

            Response headRootResponseIfModifiedSinceNow = new ResponseBuilder(headRootRequestIfModifiedSinceNow).setEtag().build();
            Assertions.assertTrue(headRootResponseIfModifiedSinceNow.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootRequestIfModifiedSinceNow.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfModifiedSinceNow.getData());

            Response headRootResponseIfModifiedSinceBeginningOfTime = new ResponseBuilder(headRootRequestIfModifiedSinceBeginningOfTime).setEtag().build();
            Assertions.assertTrue(headRootResponseIfModifiedSinceBeginningOfTime.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootRequestIfModifiedSinceBeginningOfTime.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseIfModifiedSinceBeginningOfTime.getData());

            Response headRootResponseResourceNotExisting = new ResponseBuilder(headRootRequestResourceNotExisting).setEtag().build();
            Assertions.assertFalse(headRootResponseResourceNotExisting.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseResourceNotExisting.getData());

            Response headRootResponseDateInvalid = new ResponseBuilder(headRootRequestDateInvalid).setEtag().build();
            Assertions.assertTrue(headRootResponseDateInvalid.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headRootResponseDateInvalid.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headRootResponseDateInvalid.getData());

            Response headIfNotMatchAndModifiedSinceResponse = new ResponseBuilder(headIfNotMatchAndIfModifiedSince).setEtag().build();
            Assertions.assertFalse(Arrays.equals("304 Not Modified".getBytes(), headIfNotMatchAndModifiedSinceResponse.getData()));
            Assertions.assertTrue(headIfNotMatchAndModifiedSinceResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).isPresent());
            Assertions.assertEquals("ETag: 533839800", headIfNotMatchAndModifiedSinceResponse.getHeaderLine(HeaderFields.Field.ENTITIY_TAG).get());
            Assertions.assertArrayEquals("".getBytes(), headIfNotMatchAndModifiedSinceResponse.getData());

        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }
    }

}
