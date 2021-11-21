package org.vibee.integration.input;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Iterator;
import java.util.stream.Collectors;

@Component
public class JsonReducerController implements CommandLineRunner {
    @Value("classpath:data/filter.json")
    Resource resourceFile;

    @Value("classpath:data/response.json")
    Resource resourceFileTaken;

    @Override
    public void run(String... args) throws Exception {
      /*  ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(readJsonFile(resourceFile));

        JsonNode targetObj = mapper.readTree(readJsonFile(resourceFileTaken));

       Iterator<String> fieldName = actualObj.fieldNames();

      while(fieldName.hasNext()){
           fieldName.next();
      }

         targetObj.equals(actualObj);*/

    }

    String readJsonFile(Resource resource) throws Exception{
        InputStream inputStream = resource.getInputStream();
        try ( BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream)) ) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));

        }
    }

    void logTimeNow(String logName){
        Instant instant  = Instant.now();
        System.out.println(logName + "\t\t" +  instant.toString());
    }
}
