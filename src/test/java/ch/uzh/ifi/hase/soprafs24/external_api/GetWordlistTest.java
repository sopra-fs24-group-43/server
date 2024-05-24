package ch.uzh.ifi.hase.soprafs24.external_api;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ch.uzh.ifi.hase.soprafs24.external_api.GetWordlist;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class GetWordlistTest {/*



    @Test
    public void getWordlistTest() {

/*
            List<String> l1 = new ArrayList<>();
            l1.addAll(GetWordlist.getWordlist.getWordlist2("Sport"));
            List<String> l2 = new ArrayList<>(Arrays.asList());
            System.out.println(l2);
*/
/*
    }

    @Test
    public void JsonParserTest() {
        String api_response = "[{\"word\":\"specific\",\"score\":1001},{\"word\":\"fishing\",\"score\":1000},{\"word\":\"utility\",\"score\":999},{\"word\":\"zeitung\",\"score\":998},{\"word\":\"related\",\"score\":997},{\"word\":\"royal\",\"score\":996},{\"word\":\"turism\",\"score\":995},{\"word\":\"men\",\"score\":994},{\"word\":\"mirror\",\"score\":993},{\"word\":\"tourism\",\"score\":992},{\"word\":\"the\",\"score\":991},{\"word\":\"psychology\",\"score\":990},{\"word\":\"universal\",\"score\":989},{\"word\":\"vehicles\",\"score\":988},{\"word\":\"hunting\",\"score\":987},{\"word\":\"palast\",\"score\":986},{\"word\":\"climbing\",\"score\":985},{\"word\":\"stirrup\",\"score\":984},{\"word\":\"express\",\"score\":983},{\"word\":\"abstracts\",\"score\":982},{\"word\":\"dress\",\"score\":981},{\"word\":\"magazine\",\"score\":980},{\"word\":\"mistresses\",\"score\":979},{\"word\":\"diving\",\"score\":978},{\"word\":\"based\",\"score\":977},{\"word\":\"rod\",\"score\":976},{\"word\":\"tester\",\"score\":975},{\"word\":\"fish\",\"score\":974},{\"word\":\"talk\",\"score\":973},{\"word\":\"hotel\",\"score\":972},{\"word\":\"club\",\"score\":971},{\"word\":\"diver\",\"score\":970},{\"word\":\"tourers\",\"score\":969},{\"word\":\"turist\",\"score\":968},{\"word\":\"all\",\"score\":967},{\"word\":\"death\",\"score\":966},{\"word\":\"comment\",\"score\":965},{\"word\":\"scales\",\"score\":964},{\"word\":\"lite\",\"score\":963},{\"word\":\"management\",\"score\":962}]";
        List<String> wordlist = GetWordlist.JsonParser(api_response);
        List<String> actual = new ArrayList<>();
        actual.addAll(Arrays.asList("specific", "fishing", "utility", "zeitung", "related", "royal", "turism", "men", "mirror", "tourism", "the", "psychology", "universal", "vehicles", "hunting", "palast", "climbing", "stirrup", "express", "abstracts", "dress", "magazine", "mistresses", "diving", "based", "rod", "tester", "fish", "talk", "hotel", "club", "diver", "tourers", "turist"));
        for (int i = 0;i<actual.size();i++) {
            if (!(actual.contains(wordlist.get(i)))){
                System.out.println(actual.get(i));
                System.out.println(wordlist.get(i));
                throw new AssertionError();
            }
        }

    }*/

}