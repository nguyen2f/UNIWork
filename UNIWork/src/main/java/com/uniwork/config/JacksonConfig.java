//package com.uniwork.config;
//
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
//import java.time.format.DateTimeFormatter;
//import java.util.TimeZone;
//
//@Configuration
//public class JacksonConfig {
//    @Bean
//    public Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder() {
//        return new Jackson2ObjectMapperBuilder()
//                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                .deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                .timeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//    }
//}
