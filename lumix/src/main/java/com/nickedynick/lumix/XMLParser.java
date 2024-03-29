package com.nickedynick.lumix;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class XMLParser {
    private static final String ns = null;

    private String tagReply = "camrply";

    private String tagResult = "result";
    private String tagState = "state";
    private String tagMenuInfo = "menuinfo";

    private String tagTitle = "title";
    private String tagSummary = "title";
    private String tagLink = "title";

    //ToDo: Wireshark the shit out of my camera.
    //ToDo: Develop XML parser to account for all possible returned tags.
    public CamReply parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private CamReply readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        CamReply response = new CamReply();

        parser.require(XmlPullParser.START_TAG, ns, tagReply);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String topNode = parser.getName();

            if (topNode.equals(tagResult)) {
                parser.require(XmlPullParser.START_TAG, ns, tagResult);
                String result = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, tagResult);
                response.Result = result.equals("ok");
            } else if (topNode.equals(tagState)) {
                response = readState(parser, response);
            } else if (topNode.equals(tagMenuInfo)) {
                response = readMenuInfo(parser, response);
            } else {
                skip(parser);
            }
        }
        return response;
    }

    // ToDo: Process state and menu info.
    private CamReply readState(XmlPullParser parser, CamReply response) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tagResult);
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(tagTitle)) {
                title = readTitle(parser);
            } else if (name.equals(tagSummary)) {
                summary = readSummary(parser);
            } else if (name.equals(tagLink)) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return response;
    }

    // ToDo: Process state and menu info.
    private CamReply readMenuInfo(XmlPullParser parser, CamReply response) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tagResult);
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(tagTitle)) {
                title = readTitle(parser);
            } else if (name.equals(tagSummary)) {
                summary = readSummary(parser);
            } else if (name.equals(tagLink)) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return response;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagTitle);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagTitle);
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, tagLink);
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals(tagLink)) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, tagLink);
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagSummary);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagSummary);
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
