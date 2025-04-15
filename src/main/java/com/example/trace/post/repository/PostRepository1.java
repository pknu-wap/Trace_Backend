package com.example.trace.post.repository;


import com.example.trace.post.domain.Post;
import com.example.trace.auth.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository1 extends JpaRepository<Post, Long> {

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Post> findByUserOrderByCreatedAtDesc(User user);

}