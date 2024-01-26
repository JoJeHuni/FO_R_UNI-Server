package bigsanghyuk.four_uni.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private Long categoryId;
    private boolean report;
    private String title;
    private String content;
    private String imageUrl; // 이미지로 된 게시글일 때 : 이미지 URL
    private int views; // 조회수
    private int postReport; // 신고횟수
    private boolean isClassified; // 미분류

    private LocalDate postedAt; //원글 게시일 (연/월/일만 포함되도 상관없을 것 같아서)
    private LocalDateTime deadline; //원글의 이벤트 마감일시

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /* 원글 게시일
    private LocalDateTime postedAt;
     */

}
