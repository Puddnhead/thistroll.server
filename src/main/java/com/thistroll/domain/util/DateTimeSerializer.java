package com.thistroll.domain.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * Created by MVW on 7/13/2017.
 */
public class DateTimeSerializer extends StdSerializer<DateTime> {

    public DateTimeSerializer() {
        this(null);
    }

    public DateTimeSerializer(Class<DateTime> t) {
        super(t);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getMillis() + "");
    }
}
