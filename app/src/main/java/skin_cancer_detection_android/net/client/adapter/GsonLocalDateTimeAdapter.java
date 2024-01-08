package skin_cancer_detection_android.net.client.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class GsonLocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public synchronized JsonElement serialize(LocalDateTime date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(date.toString());
    }

    @Override
    public synchronized LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return LocalDateTime.parse(jsonElement.getAsString());
    }

}
