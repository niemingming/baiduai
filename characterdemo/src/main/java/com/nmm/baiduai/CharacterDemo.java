package com.nmm.baiduai;

import com.alibaba.fastjson.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class CharacterDemo {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String token = getToken();

        readFileContent(token);
    }

    /**
     * 读取图片文字
     * @param token
     */
    private static void readFileContent(String token) throws URISyntaxException, IOException, InterruptedException {
        String filepath = "E:\\workspace\\ai\\testfiles/localfile.png";
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        String param = "access_token=" + token;

        FileInputStream fis = new FileInputStream(filepath);
        byte[] bytes = fis.readAllBytes();

        String image = Base64.getEncoder().encodeToString(bytes);

        fis.close();

        param+="&image=" + URLEncoder.encode(image,"utf-8");

        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).POST(HttpRequest.BodyPublishers.ofString(param))
                .header("Conent-Type","application/x-www-form-urlencoded")
                .build();

        String res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();

        JSONObject content = JSONObject.parseObject(res);
        for (int i = 0; i < content.getJSONArray("words_result").size(); i++) {
            System.out.println(content.getJSONArray("words_result").getJSONObject(i).getString("words"));
        }

    }

    private static String getToken() throws URISyntaxException, IOException, InterruptedException {

        String apiKey = "KfTmEZxd5ozaQYran5eGSEkq",secretKey = "ROvoUytc89Q5K7eoSHbx2hpdK7voYgCa";

        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpRequest post = HttpRequest.newBuilder(new URI(url)).GET().build();
        String token = HttpClient.newHttpClient().send(post, HttpResponse.BodyHandlers.ofString()).body();

        JSONObject object = JSONObject.parseObject(token);

        return object.getString("access_token");
    }
}
