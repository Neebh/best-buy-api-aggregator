package com.neeraj.concurrency.tasks;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.neeraj.concurrency.model.Store;
import com.neeraj.concurrency.model.StoreResponse;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class StoreAggregatorTask implements Supplier<List<Store>> {

    private String url;
    private int skip;
    private int limit;

    public StoreAggregatorTask(String url, int skip, int limit) {
        this.url = url;
        this.skip = skip;
        this.limit = limit;
    }


    @Override
    public List<Store> get() {

        HttpRequestFactory requestFactory
                = new NetHttpTransport().createRequestFactory();
        HttpRequest request = null;
        try {
            GenericUrl url = new GenericUrl(this.url);

            url.put("$limit", limit);
            url.put("$skip", skip);
            request = requestFactory.buildGetRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StoreResponse response = null;
        try {
            String rawResponse = request.execute().parseAsString();
            Gson gson = new Gson();
            response = gson.fromJson(rawResponse, StoreResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.getData();

    }
}
