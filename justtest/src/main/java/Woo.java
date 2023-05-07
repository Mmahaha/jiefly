import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

enum EnumTest {
    HH, LL, KK
}

public class Woo {
    public static void main(String[] args) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(new TableSerializer());
//        module.addDeserializer(Table.class, new TableDeserializer());
//        objectMapper.registerModule(module);
//        System.out.println(objectMapper.readValue("", Table.class));
        List<String> list = new ArrayList<String>() {
        };
        System.out.println();

//        System.out.println(JSON.toJSONString(table));
    }


    public static class TableSerializer extends JsonSerializer<Table<?, ?, ?>> {
        @Override
        public void serialize(final Table<?, ?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeObject(value.rowMap());
        }


        @Override
        public Class<Table<?, ?, ?>> handledType() {
            return (Class) Table.class;
        }
    } // end class TableSerializer

    public static class TableDeserializer extends JsonDeserializer<Table<?, ?, ?>> {
        @Override
        public Table<?, ?, ?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final ImmutableTable.Builder<Object, Object, Object> tableBuilder = ImmutableTable.builder();
            final Map<Object, Map<Object, Object>> rowMap = jp.readValueAs(Map.class);
            for (final Map.Entry<Object, Map<Object, Object>> rowEntry : rowMap.entrySet()) {
                final Object rowKey = rowEntry.getKey();
                for (final Map.Entry<Object, Object> cellEntry : rowEntry.getValue().entrySet()) {
                    final Object colKey = cellEntry.getKey();
                    final Object val = cellEntry.getValue();
                    tableBuilder.put(rowKey, colKey, val);
                }
            }
            return tableBuilder.build();
        }
    } // end class TableDeserializer

}