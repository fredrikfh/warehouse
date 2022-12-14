package data.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import data.DataUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Serializer for encoding LocalDateTime in JSON.
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(value.format(DataUtils.dateTimeFormatter));
  }
}
