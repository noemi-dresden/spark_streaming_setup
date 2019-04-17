package examplePackage.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;


public class RumDeserializer extends StdDeserializer<Rum> {

    public RumDeserializer(){
        this(null);
    }

    public RumDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Rum deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {

        JsonNode rumNode = jp.getCodec().readTree(jp);
        Rum rum = new Rum();

        rum.setTimestamp(rumNode.get("header").get("timeStamp").asDouble());

        rum.setUser(rumNode.get("payload").get("user").get("email").textValue());

        rum.setEvent(rumNode.get("payload").get("event").textValue());

        rum.setKeyword(rumNode.get("payload").get("keyword").textValue());
        return rum;
    }
}
