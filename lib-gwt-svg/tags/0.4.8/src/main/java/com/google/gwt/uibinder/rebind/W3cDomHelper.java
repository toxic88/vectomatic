/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.uibinder.rebind;

import com.google.gwt.core.ext.TreeLogger;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Simplifies instantiation of the w3c XML parser, in just the style that
 * UiBinder likes it. Used by both prod and test.
 */
public class W3cDomHelper {
  private final SAXParserFactory factory;
  private final TreeLogger logger;

  public W3cDomHelper(final TreeLogger logger) {
    this.logger = logger;
    factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
  }

  /**
   * Creates an XML document model with the given contents. Nice for testing.
   * 
   * @param string the document contents
   */
  public Document documentFor(String string) throws SAXException, IOException {
    try {
      // laaglu
        factory.setFeature("http://xml.org/sax/features/xmlns-uris", true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      // laaglu
      W3cDocumentBuilder handler = new W3cDocumentBuilder(logger);
      SAXParser parser = factory.newSAXParser();
      parser.parse(new InputSource(new StringReader(string)), handler);
      return handler.getDocument();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public Document documentFor(URL url) throws SAXParseException {
    try {
      // laaglu
      factory.setFeature("http://xml.org/sax/features/xmlns-uris", true);
      factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      // laaglu
      W3cDocumentBuilder handler = new W3cDocumentBuilder(logger);
      SAXParser parser = factory.newSAXParser();
      InputStream stream = url.openStream();
      InputSource input = new InputSource(stream);
      input.setSystemId(url.toExternalForm());
      parser.parse(input, handler);

      return handler.getDocument();
    } catch (SAXParseException e) {
      // Let SAXParseExceptions through.
      throw e;
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}
