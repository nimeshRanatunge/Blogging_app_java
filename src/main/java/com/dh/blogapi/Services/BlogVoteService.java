package com.dh.blogapi.Services;

import com.dh.blogapi.DTOs.Blog.VoteSummeryDto;
import com.dh.blogapi.Interfaces.IBlogVoteRepository;
import com.dh.blogapi.Models.BlogVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogVoteService  {
    @Autowired
    private IBlogVoteRepository iBlogVoteRepository;

    public List<BlogVote> findAll(){ return iBlogVoteRepository.findAll(); }
    public BlogVote save( BlogVote bv ){ return iBlogVoteRepository.save(bv); }

    public VoteSummeryDto getVoteSummery(Integer id){

        VoteSummeryDto sum = new VoteSummeryDto();

        sum.setLikes(iBlogVoteRepository.countLikeVotes(id));
        sum.setDislikes(iBlogVoteRepository.countDisLikeVotes(id));

        return sum;
    }

    public Integer getVoteByUser(Integer blogId , String username){ return iBlogVoteRepository.getVoteByUser(blogId , username); }

    public void updateBlogVote( Integer vote ,Integer blogId , String username ){
        iBlogVoteRepository.updateBlogVote(vote ,blogId , username);
    }

}
