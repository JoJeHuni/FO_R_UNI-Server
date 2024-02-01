package bigsanghyuk.four_uni.post.controller;

import bigsanghyuk.four_uni.CommonResponse;
import bigsanghyuk.four_uni.post.domain.entity.Post;
import bigsanghyuk.four_uni.post.dto.response.GetPostResponse;
import bigsanghyuk.four_uni.post.service.PostService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 단 건 조회
    @GetMapping("/v1/posts/{postId}")
    public ResponseEntity<GetPostResponse> getDetail(@PathVariable Long postId) {
        Post post = postService.getDetail(postId);

        GetPostResponse getPostResponse = GetPostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .views(post.getViews())
                .build();

        return new ResponseEntity(getPostResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/posts/unclassified")
    public ResponseEntity<Result<List<Post>>> getUnclassified() {
        List<Post> unClassified = postService.getUnClassifiedLists();
        return ResponseEntity.ok().body(new Result<>(unClassified, unClassified.size()));
    }

    @GetMapping("/v1/posts/filter")
    public ResponseEntity<Result<List<Post>>> getByFiltered(@Valid @RequestBody List<Long> categoryIds) {
        List<Post> filtered = postService.getFilteredPosts(categoryIds);
        return ResponseEntity.ok().body(new Result<>(filtered, filtered.size()));
    }

    @Getter @Setter
    public static class Result<T> {
        private T data;
        private int count;

        public Result(T data, int count) {
            this.data = data;
            this.count = count;
        }
    }
}
