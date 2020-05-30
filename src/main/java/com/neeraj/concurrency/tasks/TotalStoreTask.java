package com.neeraj.concurrency.tasks;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.neeraj.concurrency.model.StoreResponse;

import java.io.IOException;
import java.util.function.Supplier;

public class TotalStoreTask implements Supplier<Integer> {

    private String url;

    public TotalStoreTask(String url) {
        this.url = url;
    }


    @Override
    public Integer get() {
       HttpRequestFactory requestFactory
                = new NetHttpTransport().createRequestFactory();

        HttpRequest request = null;
        try {
            request = requestFactory.buildGetRequest(
                    new GenericUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String rawResponse = request.execute().parseAsString();
            Gson gson = new Gson();
            StoreResponse response = gson.fromJson(rawResponse, StoreResponse.class);
            return response.getTotal();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
