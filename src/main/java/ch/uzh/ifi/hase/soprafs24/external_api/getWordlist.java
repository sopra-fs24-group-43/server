package ch.uzh.ifi.hase.soprafs24.external_api;


import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;


public class getWordlist {
    private getWordlist() {}

    //scam XD
    /*public static List<String> getWords(String genre) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://twinword-word-associations-v1.p.rapidapi.com/associations/?entry="+genre))
                    .header("X-RapidAPI-Key", "99c8d838cdmsh893fe75567c4b4fp1fd315jsn278211498350")
                    .header("X-RapidAPI-Host", "twinword-word-associations-v1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            List<String> list = parser(response);
            //List<String> wordlist = JsonParser(response);
            System.out.println(list);
            return list;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "error occurred while fetching wordlist" + e.getMessage());
        }}
    */
    public static List<String> getWordlist(String genre) {
        try {
            final String uri = "https://api.datamuse.com/words?rel_jja="+genre;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            List<String> wordlist = JsonParser(result);
            System.out.println(wordlist);
            return wordlist;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "error occurred while fetching wordlist" + e.getMessage());
        }
    }
    public static List<String> JsonParser(String json) {
        ArrayList<String> wordlist2 = new ArrayList<>();
        String[] wordlist;
        String json1=json.substring(1,json.length()-1);
        wordlist = json1.split(",");
        for (int i = 0; i<wordlist.length;i++) {
            if (i%2!=1) {
                wordlist2.add(wordlist[i].substring(8, wordlist[i].length()));
            }
        }
        return wordlist2;
    }/*//old
    public static List<String> parser(HttpResponse response) {
        String list = response.toString();
        List<String> list3 = new ArrayList<>();
        String[] list2 = list.split(":");
        for (int i = 0; i<list2.length;i++) {
            if (i==6) {
                list3.add("[");
                list3.add(list2[i]);
                list3.add("]");
            }
        }
        return list3;
    }*/
}

