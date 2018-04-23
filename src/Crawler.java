
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.math.*;
import org.json.JSONArray;
import org.json.JSONObject;
public class Crawler {

    private final String USER_AGENT = "Mozilla/5.0";
    private BigDecimal maxBalance;
    public static void main(String[] args) throws Exception {
        count = 0;
        Crawler http = new Crawler();
        http.maxBalance = BigDecimal.valueOf(0l);
        System.out.println("Testing 1 - Send Http GET request");
        http.getUsers();
    }
static int count;
    // HTTP GET request
    public StringBuffer sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        response.append("{\"values\":");
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        response.append("}");
        in.close();
        return response;
    }

    public void getUsers() throws Exception {
        int since = 0;
        Boolean flag = true;
        StringBuffer url = new StringBuffer();
        url.append("https://blockchain.info/rawblock/") ;
        url.append("000000000000000000005d7af58f37b71a4888fe8310840cbd58ecf587bb4098");
        List<String> res = null;
        while(flag) {
//            url = "https://api.github.com/users?since=" + since + "&client_id=558758e6355571fbb7d5&client_secret=5bc9e551b97ecdee77f93451d50e0f536eb09a8c";
//            url = "https://blockchain.info/latestblock";
            StringBuffer response = sendGet(url.toString());
            JSONObject json = new JSONObject(response.toString());
            JSONObject values = json.getJSONObject("values");
            String[] keys =   JSONObject.getNames(values);
            for (String key: keys) {
                System.out.println("The " + key + " is "+  values.get(key));
                if(key.equals("tx")){
                    JSONArray transcArray = values.getJSONArray(key);
                    res  = findAllAddress(transcArray);
                    System.out.println();
                }
                if(key.equals("prev_block")){
                    url = new StringBuffer();
                    url.append("https://blockchain.info/rawblock/") ;
                    url.append(values.getString("prev_block"));
                }
            }
        }
//		FileWriter file = new FileWriter("test.json");
//		file.write(response.toString());
//		file.flush();
//		file.close();
    }
    public List<String> findAllAddress(JSONArray transcArray ){
//        getTheBalance(new String[]{});
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < transcArray.length(); i++) {
            JSONObject obj = transcArray.getJSONObject(i);
            JSONArray  inputsArray= obj.getJSONArray("inputs");
            JSONArray  outputArray = obj.getJSONArray("out");
            //Iterate the inputArray
            findAddressInArray(res,inputsArray,"prev_out");
            findAddressInArray(res, outputArray, "out");
        }
        composateUrls(res);
        return res;
    }
    void findAddressInArray(List<String> res,JSONArray array,String keyType){
        for (int j = 0; j < array.length(); j++) {
            JSONObject tempObj = array.getJSONObject(j);
            JSONObject preAdd ;
            if(keyType.equals("prev_out")){
                try{
                    preAdd = tempObj.getJSONObject(keyType);
                }catch (Exception e){
                    preAdd = null;
                }
                if(preAdd !=null){
                    String address = preAdd.get("addr").toString();
                    if(!res.contains(address))
                        res.add(address);
                }
            }else{
                String address = "";
                try{
                     address = tempObj.get("addr").toString();
                }catch(Exception e){
                    System.out.println("ss");
                }

                if(!res.contains(address))
                    res.add(address);
            }
        }
    }

    void composateUrls(List<String> address){
        StringBuffer mulAddr = new StringBuffer();
        String url = "https://blockchain.info/multiaddr?active=";
        mulAddr.append(address.get(0));
            for (int i = 1; i <address.size()  ; i++) {
                if(address.get(i).length()>35)
                    continue;
                if(i%100 == 0){
                    //sendURL
                    if(mulAddr.charAt(0) == '|')
                        getTheBalance(url + mulAddr.substring(1,mulAddr.length()-1));
                    else
                        getTheBalance(url + mulAddr.substring(0,mulAddr.length()-1));
                    mulAddr = new StringBuffer();
                    mulAddr.append(address.get(i));
                    mulAddr.append("|");
                }
                mulAddr.append(address.get(i));
                mulAddr.append("|");
                }
                if(mulAddr.charAt(0) == '|')
                 getTheBalance(url + mulAddr.substring(1,mulAddr.length()-1));
               else
                    getTheBalance(url + mulAddr.substring(0,mulAddr.length()-1));

    }
    BigDecimal getTheBalance(String url){
//        String url = "https://blockchain.info/rawaddr/" + address;
//        String url = "https://blockchain.info/multiaddr?active=";
        try {
        StringBuffer response = sendGet(url);
        JSONObject json = new JSONObject(response.toString());
        JSONObject values = json.getJSONObject("values");
        JSONArray addss = values.getJSONArray("addresses");
            for (int i = 0; i < addss.length(); i++) {
                JSONObject obj = addss.getJSONObject(i);
                //在这我想把所有的OBJ 作为两个表 写进数据库。
                BigDecimal  temp = BigDecimal.valueOf(Long.parseLong(addss.getJSONObject(i).get("final_balance").toString()));
                if(temp.compareTo(maxBalance) == 1){
                    maxBalance = temp;
                }
            }
        }
     catch (Exception e) {
        e.printStackTrace();
    }
    return null;
    }
    void writeIntoDatabase(JSONObject obj ) {
        Connection con;
    }
}