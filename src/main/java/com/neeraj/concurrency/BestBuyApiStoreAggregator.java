package com.neeraj.concurrency;

import com.neeraj.concurrency.tasks.CombineAllCsv;
import com.neeraj.concurrency.tasks.StoreAggregatorTask;
import com.neeraj.concurrency.tasks.TotalStoreTask;
import com.neeraj.concurrency.tasks.WriteToCsvTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BestBuyApiStoreAggregator {

    private static final String BEST_BUY_STORE_API = "http://localhost:3030/stores";
    private static  final int pageLimit = 25;

    public static void main(String args[]) throws InterruptedException, ExecutionException, TimeoutException {

        String URL = System.getenv("URL") !=null ? System.getenv("URL") : BEST_BUY_STORE_API;

        long startTime = System.currentTimeMillis();
        CompletableFuture<Integer> totalFuture = CompletableFuture
                .supplyAsync(new TotalStoreTask(URL));
            Integer totalStores = totalFuture.get(30, TimeUnit.SECONDS);
            System.out.println("Total Stores from first api " + totalStores);


            int numberOfPages = totalStores / pageLimit;
            List<CompletableFuture<String>> completableFutures = new ArrayList<>();

            IntStream.range(0, numberOfPages).forEach((page) -> {
                CompletableFuture<String> totalStoreFuture = CompletableFuture
                        .supplyAsync(new StoreAggregatorTask(URL, page * pageLimit, pageLimit))
                        .thenApplyAsync(new WriteToCsvTask());
                completableFutures.add(totalStoreFuture);
            });


            CompletableFuture<String> lastStore = CompletableFuture
                    .supplyAsync(new StoreAggregatorTask(URL, numberOfPages * pageLimit, pageLimit))
                    .thenApplyAsync(new WriteToCsvTask());

            completableFutures.add(lastStore);

            List<String> csvFileNames = completableFutures.stream()
                    .map(BestBuyApiStoreAggregator::apply)
                    .collect(Collectors.toList());

            CompletableFuture.runAsync(new CombineAllCsv(csvFileNames)).join();

            long endTime = System.currentTimeMillis();
            System.out.println("TOTAL TIME" + (endTime - startTime));
    }

    private static String apply(CompletableFuture<String> fut) {
        try {
            return fut.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}


