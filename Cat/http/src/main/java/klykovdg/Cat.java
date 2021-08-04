package klykovdg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;
import java.util.*;

public class Cat {
    public static final ObjectMapper mapper = new ObjectMapper();
    //    {
    //        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    //    }
    //};

    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats");
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        List<MyСonverter> cats = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<MyСonverter>>() {});
        cats.stream()
                .filter(value -> value.getUpvotes() != null && value.getUpvotes() > 0)
                .forEach(System.out::println);
    }
}
