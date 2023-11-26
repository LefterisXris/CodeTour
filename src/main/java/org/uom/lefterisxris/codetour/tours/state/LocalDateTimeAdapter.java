package org.uom.lefterisxris.codetour.tours.state;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 26/11/2023
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

   private static final Logger LOG = Logger.getInstance(LocalDateTimeAdapter.class);
   private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   @Override
   public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
      if (localDateTime == null)
         jsonWriter.nullValue();
      else
         jsonWriter.value(localDateTime.format(df));
   }

   @Override
   public LocalDateTime read(JsonReader jsonReader) throws IOException {
      if (jsonReader.peek() == JsonToken.NULL) {
         jsonReader.nextNull();
      } else {
         try {
            return LocalDateTime.parse(jsonReader.nextString(), df);
         } catch (Exception e) {
            LOG.error("Could not parse Datetime value!", e);
         }
      }
      return null;
   }
}