package alabno.wserver;

import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.junit.Test;

import alabno.wserver.JsonParser;

public class JsonParserTest {

	@Test
	public void invalidJsonObject() {
		JsonParser the_parser = new JsonParser("<xml ?>");
		
		assertTrue(!the_parser.isOk());
	}
	
	@Test
	public void simpleObjectParse() {
		
		String input = "{\r\n\"type\": \"models\",\r\n\"organization\": \"test\"\r\n}";
		
		JsonParser the_parser = new JsonParser(input);
		
		assertTrue(the_parser.isOk());
		
		assertEquals(the_parser.getString("type"), "models");
		
		assertEquals(the_parser.getString("organization"), "test");
		
		assertEquals(the_parser.getString("foo"), null);
		
	}
	
	@Test
	public void arrayParse() {
		
		String input = "{\r\n\"type\": \"models\",\r\n\"organization\": [\"1\", \"2\"]\r\n}";
		
		JsonParser the_parser = new JsonParser(input);
		
		assertTrue(the_parser.isOk());
		
		assertEquals(the_parser.getString("organization"), null);
		
		assertTrue(the_parser.getArray("organization") instanceof JSONArray);
	}

}
