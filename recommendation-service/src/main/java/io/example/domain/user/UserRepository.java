package io.example.domain.user;

import io.example.domain.friend.entity.RankedUser;
import io.example.domain.user.entity.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link User} data and friend connections.
 *
 * @author Kenny Bastani
 */
@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    User findUserByUserId(Long userId);

    @Query("MATCH (userA:User)-[r:FRIEND]->(userB:User) " +
            "WHERE userA.userId=$fromId AND userB.userId=$toId " +
            "DELETE r")
    void removeFriend(Long fromId, Long toId);

    @Query("MATCH (userA:User), (userB:User) " +
            "WHERE userA.userId=$fromId AND userB.userId=$toId " +
            "CREATE (userA)-[:FRIEND { createdAt: $createdAt, lastUpdated: $lastUpdated }]->(userB)")
    void addFriend(Long fromId, Long toId, Long createdAt, Long lastUpdated);

    @Query("MATCH (userA:User), (userB:User)\n" +
            "WHERE userA.userId=$fromId AND userB.userId=$toId\n" +
            "MATCH (userA)-[:FRIEND]-(fof:User)-[:FRIEND]-(userB)\n" +
            "RETURN DISTINCT fof")
    Streamable<User> mutualFriends(Long fromId, Long toId);

    @Query("MATCH (me:User {userId: $userId})-[:FRIEND]-(friends),\n" +
            "\t(nonFriend:User)-[:FRIEND]-(friends)\n" +
            "WHERE NOT (me)-[:FRIEND]-(nonFriend)\n" +
            "WITH nonFriend, count(nonFriend) as mutualFriends\n" +
            "RETURN nonFriend as user, mutualFriends as weight\n" +
            "ORDER BY weight DESC")
    <T> Streamable<T> recommendedFriends(Long userId, Class<T> clazz);
}
