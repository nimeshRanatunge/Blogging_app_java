package com.dh.blogapi.Interfaces;

import com.dh.blogapi.Models.BlogVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface IBlogVoteRepository extends JpaRepository<BlogVote , Integer> {
    @Query(value = "SELECT COUNT(vote) FROM blogs_vote WHERE vote = 1 AND blogId = :blogId" , nativeQuery = true)
    Integer countLikeVotes( @Param("blogId") Integer blogId );

    @Query(value = "SELECT COUNT(vote) FROM blogs_vote WHERE vote = -1 AND blogId = :blogId" , nativeQuery = true)
    Integer countDisLikeVotes( @Param("blogId") Integer blogId );

    @Query(value = "SELECT vote FROM blogs_vote WHERE blogId = :blogId AND username = :username" , nativeQuery = true)
    Integer getVoteByUser(@Param("blogId") Integer blogId , @Param("username") String username);

    @Modifying
    @Query(value = "UPDATE blogs_vote SET vote = :vote WHERE username = :username AND blogId = :blogId" , nativeQuery = true)
    @Transactional
    void updateBlogVote( @Param("vote") Integer vote , @Param("blogId") Integer blogId , @Param("username") String username);

    @Modifying
    @Query(value = "INSERT INTO blogs_vote ( blogId , username , vote ) VALUES ( :blogId , :username , :vote ) " ,nativeQuery = true)
    @Transactional
    void saveBlogVote( @Param("vote") Integer vote , @Param("blogId") Integer blogId , @Param("username") String username);

}
