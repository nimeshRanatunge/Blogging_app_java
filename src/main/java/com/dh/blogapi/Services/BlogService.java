package com.dh.blogapi.Services;

import com.dh.blogapi.DTOs.Blog.VoteSummeryDto;
import com.dh.blogapi.Interfaces.IBlogRepository;
import com.dh.blogapi.Models.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {
    @Autowired
    private IBlogRepository repo;

    public List<Blog> getAll(){ return repo.findAll(); }

    public List<Blog> getBlogsByStatus(String status){ return repo.findBlogsByStatus(status); }
    public Blog getBlogByStatus(Integer id , String status) { return repo.findBlogByStatus(id , status); }

    public Blog save(Blog blog){ return repo.save(blog); }

    public Blog get(int id){ return repo.findById(id).get(); }

    public void delete(int id){  repo.deleteById(id); }

    public void setStatus(Integer id, String status) {  repo.updateStatus(id , status);  }

    public List<Blog> getBlogsByOwner(String owner) {  return repo.findBlogsByOwner(owner); }

//    public VoteSummeryDto getVoteSummery(Integer id){
//
//        VoteSummeryDto sum = new VoteSummeryDto();
//
//        sum.setLikes(repo.countLikeVotes(id));
//        sum.setDislikes(repo.countDisLikeVotes(id));
//
//        return sum;
//    }

//    public Integer getVoteByUser(Integer blogId , String username){ return repo.getVoteByUser(blogId , username); }
//
//    public void updateBlogVote( Integer vote ,Integer blogId , String username ){
//        repo.updateBlogVote(vote ,blogId , username);
//    }
}
