package ch.uzh.ifi.hase.soprafs24.controller.wscontroller.utils;
import java.security.Principal;
public class TestStompPrincipal implements Principal{
    String name;

    public TestStompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
