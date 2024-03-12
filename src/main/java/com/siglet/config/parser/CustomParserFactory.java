package com.siglet.config.parser;

import java.io.IOException;
import java.io.Reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Factory used to retrieve the last-created parser.
 * 
 * NOTE: THIS IS NO LONGER THREAD SAFE. USE ONLY INSIDE A SINGLE THREAD.
 * 
 * @author csmith
 *
 */
public class CustomParserFactory extends JsonFactory {

	private static final long serialVersionUID = -7523974986510864179L;
	private JsonParser parser;

	public JsonParser getParser() {
		return this.parser;
	}

	@Override
	public JsonParser createParser(Reader r) throws IOException, JsonParseException {
		parser = super.createParser(r);
		return parser;
	}

	@Override
	public JsonParser createParser(String content) throws IOException, JsonParseException {
		parser = super.createParser(content);
		return parser;
	}


}
