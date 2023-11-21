package com.dh.blogapi.Controllers;

import com.dh.blogapi.DTOs.Blog.BlogCreateDto;
import com.dh.blogapi.DTOs.Blog.BlogDto;
import com.dh.blogapi.DTOs.Blog.VoteSummeryDto;
import com.dh.blogapi.Models.Blog;
import com.dh.blogapi.Models.BlogVote;
import com.dh.blogapi.Services.BlogService;
import com.dh.blogapi.Services.BlogVoteService;
import com.dh.blogapi.Utility.JwtDecodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class BlogController {

    @Autowired
    private JwtDecodeService jwtDecodeService;

    @Autowired
    private BlogService service;

    @Autowired
    private BlogVoteService blogVoteService;

    @PostMapping("/blog")
    public ResponseEntity<?> createArticle(@RequestBody BlogCreateDto articleCreateDto){
        try{
            ModelMapper modelMapper = new ModelMapper();
            Blog a = modelMapper.map( articleCreateDto , Blog.class );
            a.setCreatedDate(new Date());
            a.setStatus("waiting");
            a.setViews(0);
            a.setOwner(jwtDecodeService.decode().getUsername());

            Blog createdArticle = service.save(a);

            return new ResponseEntity<>(modelMapper.map(createdArticle , BlogDto.class) , HttpStatus.OK );
        }catch (Exception e){
            return new ResponseEntity<>( "Internal Sever Error"  , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/blogs")
    public ResponseEntity<?> fetchBlogs(@RequestBody String status){

        if( jwtDecodeService.decode() != null ) {
            if(jwtDecodeService.decode().getPermissionLevel() == "user"){
                status = "approved";
            }
        }else{
            status = "approved";
        }

        try {
            return new ResponseEntity<>( service.getBlogsByStatus(status).stream().map( blog -> {
                ModelMapper modelMapper = new ModelMapper();
                return modelMapper.map(blog , BlogDto.class);
            } ).collect(Collectors.toList()) , HttpStatus.OK );
        }catch (Exception e){
            return new ResponseEntity<>( "Internal Server Error" , HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @PostMapping("blog/{id}/setStatus")
    public ResponseEntity<?> setStatus(@PathVariable Integer id , @RequestBody String status ){
        if(Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin")){
            try{
                Blog b = service.get(id);
                service.setStatus(b.getId() , status);
            }catch (Exception e){
                return new ResponseEntity<>("Blog Not Found" , HttpStatus.NOT_FOUND);
            }
        }else{
            return new ResponseEntity<>("Permission denied" , HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>("Server Error" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Integer id){
        try{

            String permissionLevel = jwtDecodeService.decode().getPermissionLevel();

            ModelMapper modelMapper = new ModelMapper();
            try {
                Blog b = service.get(id);

                if(Objects.equals(b.getStatus(), "approved") || Objects.equals( permissionLevel , "admin") ){
                    return new ResponseEntity<>(modelMapper.map( b , BlogDto.class) , HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Permission Denied" , HttpStatus.FORBIDDEN);
                }
            }catch (Exception e){
                return new ResponseEntity<>("Blog Not Fount" , HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity<>("Server Error" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("blogs/{owner}")
    public ResponseEntity<?> getBlogsByOwner(@PathVariable String owner){

        try{
            try {
                List<Blog>  blogs = service.getBlogsByOwner(owner);
                return new ResponseEntity<>(blogs.stream().map(blog -> {
                    if(jwtDecodeService.decode() != null){
                        if(Objects.equals(jwtDecodeService.decode().getPermissionLevel() , "admin")
                                || Objects.equals(blog.getStatus() , "approved")
                                || Objects.equals(jwtDecodeService.decode().getUsername() , blog.getOwner())  ){
                            ModelMapper modelMapper = new ModelMapper();
                            return modelMapper.map(blog , BlogDto.class);
                        }
                    }else{
                        if(Objects.equals(blog.getStatus() , "approved")){
                            ModelMapper modelMapper = new ModelMapper();
                            return modelMapper.map(blog , BlogDto.class);
                        }
                    }
                    return null;

                }).collect(Collectors.toList()) , HttpStatus.OK);
            }catch (Exception e){
                return new ResponseEntity<>("Blogs Not Fount" , HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>("Internal Server Error" , HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("blog/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id){
        try{

            Blog b = service.get(id);

            if( b.getOwner() == jwtDecodeService.decode().getUsername()
                    || jwtDecodeService.decode().getPermissionLevel() == "admin"  ){
                service.delete(id);
                return new ResponseEntity<>("Blog Success Fully Deleted" , HttpStatus.OK );
            }else{
                return new ResponseEntity<>("Unauthorized access" , HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            return new ResponseEntity<>("Blog Not found" , HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("blog/{id}/like/{vote}")
    public ResponseEntity<?> likeHandler( @PathVariable Integer id , @PathVariable Integer vote){
        try{
            Blog b = null;

            try{
                b = service.get(id);
            }catch (Exception e){
                return new ResponseEntity<>( "Blog not found" , HttpStatus.NOT_FOUND );
            }

            if(Objects.equals(b.getStatus() , "approved")) {

                Integer fetchedVote = blogVoteService.getVoteByUser(id, jwtDecodeService.decode().getUsername());
                if (Objects.nonNull(fetchedVote)) {

                    if (Objects.equals(vote, fetchedVote) && Objects.equals(vote, 1)) {
                        blogVoteService.updateBlogVote(0, id, jwtDecodeService.decode().getUsername());
                        return new ResponseEntity<>("update vote", HttpStatus.OK);
                    } else if (Objects.equals(vote, fetchedVote) && Objects.equals(vote, -1)) {
                        blogVoteService.updateBlogVote(0, id, jwtDecodeService.decode().getUsername());
                        return new ResponseEntity<>("update vote", HttpStatus.OK);
                    } else if (Objects.equals(fetchedVote, 0)) {
                        blogVoteService.updateBlogVote(vote, id, jwtDecodeService.decode().getUsername());
                        return new ResponseEntity<>("update vote", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Something went wrong", HttpStatus.METHOD_NOT_ALLOWED);
                    }

                } else {

                    BlogVote bv = new BlogVote();
                    bv.setBlogId(id);
                    bv.setUsername(jwtDecodeService.decode().getUsername());
                    bv.setVote(vote);
                    blogVoteService.save(bv);

                    return new ResponseEntity<>("voted", HttpStatus.OK);
                }
            }else{
                return new ResponseEntity<>( "Blog not approved yet" , HttpStatus.METHOD_NOT_ALLOWED );
            }

        }catch (Exception e){
            return new ResponseEntity<>( "Internal Server Error" , HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}
