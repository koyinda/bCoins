import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        double findRate = 0.02;
        // System.out.println("Hello, World!");
        PriorityQueue<Order> bidHeap = new PriorityQueue<>(
                Comparator.comparingDouble(Order::getRate).thenComparingDouble(Order::getValue));
        PriorityQueue<Order> askHeap = new PriorityQueue<>(
                Comparator.comparingDouble(Order::getRate).thenComparingDouble(Order::getValue));
        PriorityQueue<Order> bidHeapL = new PriorityQueue<>(
                Comparator.comparingDouble(Order::getRate).thenComparingDouble(Order::getValue));
        PriorityQueue<Order> askHeapL = new PriorityQueue<>(
                Comparator.comparingDouble(Order::getRate).thenComparingDouble(Order::getValue));

        try {
            HttpURLConnection getCall = (HttpURLConnection) new URL(
                    "https://www.binance.com/api/v3/depth?symbol=BTCNGN&limit=100").openConnection();
            getCall.setRequestMethod("GET");
            InputStream readString = getCall.getInputStream();

            String result = new BufferedReader(new InputStreamReader(readString)).lines()
                    .collect(Collectors.joining("\n"));

            String[] ar1 = result.split("bids");
            String[] ar2 = ar1[1].split("asks");
            ar2[0] = ar2[0].replace("\"", "");
            ar2[0] = ar2[0].replace("\\[", "");
            ar2[0] = ar2[0].replace("\\]", "");
            ar2[0] = ar2[0].substring(3, ar2[0].length() - 3);
            String[] ar3 = ar2[0].split("\\],\\[");
            for (String string : ar3) {
                String[] ar4 = string.split(",");
                double value = Double.parseDouble(ar4[0]);
                double rate = Double.parseDouble(ar4[1]);
                Order bid = new Order(value, rate);
                bidHeap.offer(bid);
            }
            ar2[1] = ar2[1].replace("\"", "");
            ar2[1] = ar2[1].replace("\\[", "");
            ar2[1] = ar2[1].replace("\\]", "");
            ar2[1] = ar2[1].substring(3, ar2[1].length() - 3);
            String[] ar5 = ar2[1].split("\\],\\[");
            for (String string : ar5) {
                String[] ar4 = string.split(",");
                double value = Double.parseDouble(ar4[0]);
                double rate = Double.parseDouble(ar4[1]);
                Order bid = new Order(value, rate);
                askHeap.offer(bid);
            }
            // System.out.println(ar2[0]);

            // String[] ar5 = ar3[1].split(",");
            // value = Double.parseDouble(ar5[0]);
            // rate = Double.parseDouble(ar5[1]);
            // Order ask = new Order(value, rate);
            // askHeap.offer(ask);

            // }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

        try {
            HttpURLConnection getLuno = (HttpURLConnection) new URL("https://ajax.luno.com/ajax/1/trades?pair=XBTNGN")
                    .openConnection();
            getLuno.setRequestMethod("GET");
            InputStream readString = getLuno.getInputStream();

            // String json = new
            // InputStreamReader(readString)).lines().collect(Collectors.joining("\n");
            String resultL = new BufferedReader(new InputStreamReader(readString)).lines()
                    .collect(Collectors.joining("\n"));

            resultL = resultL.substring(12, resultL.length() - 3);
            String[] arLuno1 = resultL.split("\\},\\{");
            // System.out.println(arLuno1[2]);
            for (String entry : arLuno1) {
                String[] arLuno2 = entry.split(",");
                double valueLuno = Double.parseDouble(arLuno2[2].substring(9, arLuno2[2].length() - 1));
                double rateLuno = Double.parseDouble(arLuno2[3].substring(10, arLuno2[3].length() - 1));
                Order bidLuno = new Order(valueLuno, rateLuno);
                if (arLuno2[4].equals("\"is_buy\":true")) {
                    bidHeapL.offer(bidLuno);
                } else {
                    askHeapL.offer(bidLuno);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        double biTotalBuy = 0;
        double luTotalBuy = 0;
        double biTotalSell = 0;
        double luTotaSell = 0;
        List<Order> buyOptionL = new ArrayList<>();
        List<Order> buyOptionB = new ArrayList<>();
        List<Order> sellOptionL = new ArrayList<>();
        List<Order> sellOptionB = new ArrayList<>();
        while (biTotalBuy < findRate) {
            Order min = bidHeap.poll();
            biTotalBuy += min.getRate();
            buyOptionB.add(min);

        }
        while (biTotalSell < findRate) {
            Order min = askHeap.poll();
            biTotalSell += min.getRate();
            sellOptionB.add(min);

        }
        // System.out.println(buyOptionB);
        while (luTotalBuy < findRate) {
            Order min = bidHeapL.poll();
            luTotalBuy += min.getRate();
            buyOptionL.add(min);
        }
        while (luTotaSell < findRate) {
            Order min = askHeapL.poll();
            luTotaSell += min.getRate();
            sellOptionL.add(min);
        }

        if (biTotalBuy > luTotalBuy) {
            System.out.println("It is better to buy with Binance: " + buyOptionB);
        } else {
            System.out.println("It is better to buy with Luno" + buyOptionL);
        }

        if (biTotalSell < luTotaSell) {
            System.out.println("It is better to sell with Binance: " + sellOptionB);
        } else {
            System.out.println("It is better to sell with Luno" + sellOptionL);
        }
    }
}
