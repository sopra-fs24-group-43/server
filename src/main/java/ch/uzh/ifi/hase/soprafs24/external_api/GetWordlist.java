package ch.uzh.ifi.hase.soprafs24.external_api;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetWordlist {
    private GetWordlist() {}

    public  List<String> getWordlist2(String genre) { //removed the static
        try {
            final String uri = "https://api.datamuse.com/words?rel_jja="+genre;
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            List<String> wordlist = JsonParser(result);
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
        for (int i = 0; i<wordlist.length-12;i++) {
            if (i%2!=1) {
                wordlist2.add(wordlist[i].substring(8, wordlist[i].length()));
            }
        }
        return wordlist2;
    }
}