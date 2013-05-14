package gr.plato.dashboard.service.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class SimpleDateDeserializer extends JsonDeserializer<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		if (parser.getText() == null || parser.getText().trim().isEmpty()) {
			return null;
		}
		try {
			return dateFormat.parse(parser.getText());
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}
}
