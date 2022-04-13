package class101.foo.io.cache;

import class101.foo.io.Post;
import class101.foo.io.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCacheService {

    private final PostRepository postRepository;

    private Page<Post> firstPostPage;

    @Scheduled(cron = "* * * * * *")
    private void updateFirstPage() {
        firstPostPage = postRepository.findAll(
                PageRequest.of(0, 20, Sort.by("id").descending())
        );
    }

    public Page<Post> getFirstPostPage() {
        return this.firstPostPage;
    }
}

