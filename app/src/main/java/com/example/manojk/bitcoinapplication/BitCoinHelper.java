package com.example.manojk.bitcoinapplication;



import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manoj.k on 29-Aug-17.
 */

public class BitCoinHelper {

    public static final String CURRENT_PRICE_URL="https://api.coindesk.com/v1/bpi/currentprice/USD.json";
    public static final String PREVIOUS_PRICE_URL="https://api.coindesk.com/v1/bpi/historical/close.json?for=yesterday";

    public static Map<String,Float> GetBCI(){
        String BCI="0.00";
        Map<String,Float> map=new HashMap<String, Float>();
        try {
            URL urlCurr = new URL(CURRENT_PRICE_URL);
            URL urlPrev = new URL(PREVIOUS_PRICE_URL);
            HttpURLConnection con1= (HttpURLConnection) urlCurr.openConnection();
            HttpURLConnection con2=(HttpURLConnection) urlPrev.openConnection();
            if(con1.getResponseCode()!=200 && con2.getResponseCode()!=200)
                throw new RuntimeException("Failed to connect : "+con1.getResponseCode()+" "+con2.getResponseCode());

            BufferedReader buff1=new BufferedReader(new InputStreamReader(con1.getInputStream()));
            BufferedReader buff2=new BufferedReader(new InputStreamReader(con2.getInputStream()));
            String output1;
            String output2;
            StringBuffer strbuff1=new StringBuffer();
            StringBuffer strbuff2=new StringBuffer();

            while((output1=buff1.readLine())!=null){
                strbuff1.append(output1);
            }
            while((output2=buff2.readLine())!=null){
                strbuff2.append(output2);
            }
            JsonParser parser=new JsonParser();
            JsonObject object1= (JsonObject) parser.parse(strbuff1.toString());
            JsonObject object2=(JsonObject) parser.parse(strbuff2.toString());

            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal=Calendar.getInstance();
            cal.add(Calendar.DATE,-1);

            String curPrice=object1.get("bpi").getAsJsonObject().get("USD").getAsJsonObject().get("rate").toString();
            String prevPrice=object2.get("bpi").getAsJsonObject().get(dateFormat.format(cal.getTime()).toString()).toString();

            float curFloat=Float.parseFloat(curPrice);
            float prevFloat=Float.parseFloat(prevPrice);

            float rate=((prevFloat-curFloat)/prevFloat)*100;
            map.put("rate",rate);
            map.put("index",curFloat);

            buff1.close();
            buff2.close();
            con1.disconnect();
            con2.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  map;


    }


}
