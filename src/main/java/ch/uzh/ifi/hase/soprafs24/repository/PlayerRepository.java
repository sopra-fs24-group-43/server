package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("PlayerRepository")
public interface PlayerRepository extends JpaRepository<User, Long> {
    //User findUserByLobbyId(long id);
}