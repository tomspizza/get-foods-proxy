package org.susi.integration.input;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.susi.integration.CodeobeListener;
import org.susi.integration.CodeobeLog;
import org.susi.integration.dto.FoodItem;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/restaurant-service")
@EnableIntegration
public class RestaurantProxyController extends CodeobeListener {

    @Value("${restaurant.baseUrlXML}")
    String baseUrlXML;

    @Autowired
    CodeobeLog codeobeLog;

    @GetMapping(value = "getFoodItemsForShop/{shopId}")
    public ResponseEntity<List<FoodItem>> getFoodItemsForShop(@PathVariable("shopId") String shopId) throws Exception {
        // logging before message
        TextMessage tm1 = codeobeLog.logMessageBeforeProcess(shopId);

        // being processed
        List<String> processedAndSentList = process(tm1);
        logTimeNow("process , send finished -- > extraction starts");
        List<FoodItem> foodItemList = getListOfFoodItemsOnly(processedAndSentList.get(0));
        logTimeNow(" extraction ends");
        return new ResponseEntity<>(foodItemList, HttpStatus.OK);
    }


    @Override
    public List<String> process(TextMessage tm) throws JMSException {
        logTimeNow("process starts");
        TextMessage tm2 = codeobeLog.logMessageAfterProcess(tm, tm.getText());
        logTimeNow("process ends --> send start");
        try {
            List<String> sentList = send(Collections.singletonList(tm2));
            codeobeLog.logResponse(tm2, sentList.get(0));

            return sentList;
        } catch (Exception e) {
            codeobeLog.logResponseError(tm2, e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> send(List<TextMessage> tm) throws IOException {
        String msg = getPayload(tm.get(0));

        String response = getListOfFoodItems(msg);
        logTimeNow("send ends");
        return Collections.singletonList(response);
    }


    private String getListOfFoodItems(String shopId) throws IOException {
        String url = baseUrlXML + "food/getFoodItemsForShop/" + shopId;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return Objects.requireNonNull(response.body()).string();
    }


    private List<FoodItem> getListOfFoodItemsOnly(String json) throws JsonProcessingException {
        List<FoodItem> foodItemList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        if (!node.isArray()) {
            return foodItemList;
        }

        for (JsonNode foodNode : node) {
            FoodItem food = new FoodItem();
            food.setFoodItemId(foodNode.get("foodItemId").asText());
            food.setFoodItemName(foodNode.get("foodItemName").asText());
            food.setFoodItemPrice((float) foodNode.get("foodItemPrice").asDouble());
            foodItemList.add(food);
        }

        return foodItemList;
    }

    void logTimeNow(String logName){
        Instant instant  = Instant.now();
        System.out.println(logName + "\t\t" +  instant.toString());
    }
}


/*

  @Value("${restaurant.baseUrlXML}")
    String baseUrlXML;

    @Autowired
    CodeobeLog codeobeLog;

    @PostMapping(value = "getNearbyShops", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Shop>> getNearbyShopsResponse(@RequestBody LocationDTO dto) throws JsonProcessingException {

        // convert json to string
        String json = convertDtoToJson(dto);

        // logging before message
        TextMessage tm1 = codeobeLog.logMessageBeforeProcess(json);

        // being processed
        List<String> processedAndSentList = process(tm1);

        List<Shop> responseList = new ArrayList<>();
        try {
            // xml to json mapping
            XmlMapper xmlMapper = new XmlMapper();
            responseList  = xmlMapper.readValue(processedAndSentList.get(0), List.class);
        } catch (Exception ignored) {}

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Override
    public List<String> process(TextMessage tm) {
        String msg = getPayload(tm);

        // convert json to xml
        String xml;
        try {
            xml = convertJsonToXml(msg);
        } catch (JsonProcessingException e) {
            // when conversion throws error
            codeobeLog.logErrorAfterProcess(tm, e.getMessage());
            e.printStackTrace();
            return Collections.singletonList(e.getMessage());
        }


        // log after the process
        List<TextMessage> processedTmList = new ArrayList<>();
        TextMessage tm2 = codeobeLog.logMessageAfterProcess(tm, xml);
        processedTmList.add(tm2);


        // invoking send method after process
        List<String> sentList;
        try {
            sentList = send(processedTmList);
            // on success of send
            codeobeLog.logResponse(tm2, sentList.get(0));
            return sentList;
        } catch (Exception e) {
            // when send method fails
            codeobeLog.logResponseError(tm2, e.getMessage());
            return Collections.singletonList(e.getMessage());
        }
    }

    @Override
    public List<String> send(List<TextMessage> tm) throws Exception {
        String msg = getPayload(tm.get(0));
        String response = getNearbyShopsClient(msg);
        return Collections.singletonList(response);
    }


    private String getNearbyShopsClient(String xmlBody) throws IOException {
        String url = baseUrlXML + "food/getNearbyShops";

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/xml");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, xmlBody);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/xml")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body());
        return Objects.requireNonNull(response.body()).string();
    }

    private String convertJsonToXml(String jsonStr) throws JsonProcessingException {
        System.out.println(jsonStr);
        ObjectMapper jsonMapper = new ObjectMapper();

        LocationDTO locationDTOConverted = jsonMapper.readValue(jsonStr, LocationDTO.class);

        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(locationDTOConverted);
    }

    private String convertDtoToJson(LocationDTO dto) throws JsonProcessingException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.writeValueAsString(dto);
    }
 */
