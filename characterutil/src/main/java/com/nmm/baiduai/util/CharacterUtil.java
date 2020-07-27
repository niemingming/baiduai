package com.nmm.baiduai.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class CharacterUtil {


    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        File source = new File("images");
        File target = new File("target");


        if (!source.exists()) {
            System.out.println("未找到源文件目录");
            return ;
        }

        if (!target.exists()) {
            target.mkdirs();
        }
        String token = getToken();
        //遍历文件

        for (File file : source.listFiles()) {
            readFile(file,target,token);
        }


    }

    /**
     * 识别图像
     * @param file
     * @param target
     */
    private static void readFile(File file, File target,String token) throws IOException, URISyntaxException, InterruptedException {
        String filename = file.getName().toLowerCase();
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
        || filename.endsWith(".png")||filename.endsWith("bmp")) {
            String newname = filename + ".txt";
            FileInputStream fis = new FileInputStream(file);
            String image = Base64.getEncoder().encodeToString(fis.readAllBytes());
            image = URLEncoder.encode(image,"UTF-8");

            fis.close();
            String param = "access_token=" + token + "&image=" + image;

            HttpRequest post = HttpRequest.newBuilder().uri(new URI(url))
                    .POST(HttpRequest.BodyPublishers.ofString(param))
                    .header("Conent-Type","application/x-www-form-urlencoded")
                    .build();

            String res = HttpClient.newHttpClient().send(post, HttpResponse.BodyHandlers.ofString()).body();

            JSONObject result = JSONObject.parseObject(res);
            FileWriter fileWriter = new FileWriter(new File(target,newname));
            JSONArray words = result.getJSONArray("words_result");
            for (int i = 0; i < words.size(); i++) {
                String word = words.getJSONObject(i).getString("words");
                fileWriter.write(word);
                fileWriter.write("\r\n");
            }
            fileWriter.close();

        }else {
            System.out.println("不是支持的格式，无法处理");
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
