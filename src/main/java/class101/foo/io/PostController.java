package class101.foo.io;

import class101.foo.io.cache.PostCacheService;
import class101.foo.io.messagequeue.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    final int PAGE_SIZE = 20;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostCacheService postCacheService;

    @Autowired
    Producer producer;

    @Autowired
    ObjectMapper objectMapper;

    // 1. 글을 작성한다.
    @PostMapping("/post")
    public Post createPost(@RequestBody Post post) throws JsonProcessingException {
        String jsonPost = objectMapper.writeValueAsString(post);    // String 변환
        producer.sendTo(jsonPost);
        return post;
    }

    // 2-1. 글 목록을 조회한다.
    @GetMapping("/posts")
    public Page<Post> getPostList(@RequestParam(defaultValue = "1") int page) {
        if (page == 1) {
            return postCacheService.getFirstPostPage();
        } else {
            return postRepository.findAll(
                    PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").descending())
            );
        }
    }

    // 3. 글 번호로 조회
    @GetMapping("/post/{id}")
    public Post getPostById(@PathVariable("id") Long id) {
        return postRepository.findById(id).get();
    }

    // 4. 글 내용으로 검색 -> 해당 내용이 포함된 모든 글
    @GetMapping("/search")
    public List<Post> findPostsByContent(@RequestParam String content) {
        return postRepository.findByContentContains(content);
    }

}
