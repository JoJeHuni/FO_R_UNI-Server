package bigsanghyuk.four_uni.controller;

import bigsanghyuk.four_uni.comment.domain.entity.Comment;
import bigsanghyuk.four_uni.comment.repository.CommentRepository;
import bigsanghyuk.four_uni.config.TestSecurityConfig;
import bigsanghyuk.four_uni.config.jwt.JwtProvider;
import bigsanghyuk.four_uni.post.controller.PostController;
import bigsanghyuk.four_uni.post.domain.entity.Post;
import bigsanghyuk.four_uni.post.domain.entity.Scrapped;
import bigsanghyuk.four_uni.post.repository.PostRepository;
import bigsanghyuk.four_uni.post.repository.ScrappedRepository;
import bigsanghyuk.four_uni.post.service.PostService;
import bigsanghyuk.four_uni.post.service.ScrappedService;
import bigsanghyuk.four_uni.user.domain.entity.Authority;
import bigsanghyuk.four_uni.user.domain.entity.User;
import bigsanghyuk.four_uni.user.enums.CategoryType;
import bigsanghyuk.four_uni.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private ScrappedService scrappedService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ScrappedRepository scrappedRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // 목 객체 초기화
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        Post post = postRepository.save(Post.builder()
                        .id(1L)
                        .categoryType(CategoryType.ISIS)
                        .reported(false)
                        .title("testPostTitle")
                        .content("testContent")
                        .imageUrl(Collections.singletonList("testImageUrl"))
                        .views(0)
                        .postReportCount(0)
                        .isClassified(false)
                        .postedAt(LocalDate.now())
                        .deadline(LocalDate.now())
                        .noticeUrl("testNoticeUrl")
                .build());

        // 관리자용 유저
        User user = userRepository.save(User.builder()
                .id(1L)
                .email("test_email@test.com")
                .password(passwordEncoder.encode("test1111"))
                .departmentType(CategoryType.ISIS) // 컴퓨터 공학부
                .image("test_image_url")
                .nickName("test_nickname")
                .roles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()))
                .build());

        commentRepository.save(Comment.builder()
                .id(1L)
                .user(user)
                .postId(1L)
                .reported(false)
                .parent(null)
                .commentLike(0)
                .content("testComment")
                .commentReportCount(0)
                .deleted(false)
                .build());

        scrappedRepository.save(new Scrapped(user, post));
    }

    @Test
    void 게시글_등록() throws Exception {
        //given
        Authentication atc = new TestingAuthenticationToken("test_email@test.com", null, "ROLE_ADMIN");
        String accessToken = jwtProvider.createToken("test_email@test.com", 1L, Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));

        String testData =
                "[\n" +
                "    {\n" +
                "        \"category_id\": \"246\",\n" +
                "        \"title\": \"대학수학  튜터시간표\",\n" +
                "        \"content\": \"\\nTutor시간표 대학수학(1)\\n요일\\n월\\n화\\n수\\n목\\n금\\n시간\\n10:00~12:00/\\n14:00~16:00\\n튜터실\\n5-106호\\n※튜터실은5호관(자연과학대학)입니다.\\n\",\n" +
                "        \"img_url\": [],\n" +
                "        \"posted_at\": \"2024.03.28\",\n" +
                "        \"deadline\": \"2024.03.28\",\n" +
                "        \"isclassified\": true,\n" +
                "        \"notice_url\": \"https://inu.ac.kr/bbs/inu/246/385074/artclView.do\"\n" +
                "    }\n" +
                "]";

        //when, then
        mockMvc.perform(post("/add-post")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(testData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(authentication(atc)))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertThat(postRepository.findAll().contains("246"));

    }

    @Test
    void 게시글_댓글_함께_조회() throws Exception {
        //given
        Authentication atc = new TestingAuthenticationToken("test_email@test.com", null, "ROLE_ADMIN");

        String accessToken = jwtProvider.createToken("test_email@test.com", 1L, Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));

        //when, then
        mockMvc.perform(get("/posts/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(authentication(atc)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 스크랩_추가_성공() throws Exception {
        //given
        User user = userRepository.save(User.builder()
                .id(2L)
                .email("test_email2@test.com")
                .password(passwordEncoder.encode("test2222"))
                .departmentType(CategoryType.ISIS) // 컴퓨터 공학부
                .image("test_image_url2")
                .nickName("test_nickname2")
                .roles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()))
                .build());

        Authentication atc = new TestingAuthenticationToken("test_email2@test.com", null, "ROLE_USER");
        String accessToken = jwtProvider.createToken(user.getEmail(), user.getId(), user.getRoles());

        Post post = postRepository.save(new Post(2L, CategoryType.ISIS, false, "testPostTitle2", "testContent2", Collections.singletonList("testImageUrl2"), 0, 0, false, LocalDate.now(), LocalDate.now(), "testNoticeUrl2"));

        scrappedService.unScrap(user.getId(), post.getId()); // 해당 라인을 지우고 2번 실행 시 이미 스크랩된 글인지 아닌지 알 수 있다. -> scrappedRepository에 남아있기 때문
        //when, then
        mockMvc.perform(post("/posts/scrap/{postId}", post.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(authentication(atc)))
                .andExpect(status().isOk())
                .andDo(print());

        Assertions.assertThat(scrappedRepository.findAll().contains(post.getId()));
    }

//    @Test
//    @DisplayName("스크랩 취소 테스트")
//    void unScrapSuccess() throws Exception {
//        //given
//        Authentication atc = new TestingAuthenticationToken("test_email@test.com", null, "ROLE_ADMIN");
//
//        String accessToken = jwtProvider.createToken("test_email@test.com", 1L, Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));
//
//        //when, then
//        ResultActions actions = mockMvc.perform(delete("/posts/unscrap/{postId}", 1)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .with(authentication(atc))
//        );
//
//        actions
//                .andExpect(status().isOk())
//                .andDo(print());
//    }

/*
사용할 Json 형태의 게시글
[
    {
        "category_id": "246",
        "title": "대학수학  튜터시간표",
        "content": "\nTutor시간표 대학수학(1)\n요일\n월\n화\n수\n목\n금\n시간\n10:00~12:00/\n14:00~16:00\n튜터실\n5-106호\n※튜터실은5호관(자연과학대학)입니다.\n",
        "img_url": [],
        "posted_at": "2024.03.28",
        "deadline": "2024.03.28",
        "isclassified": true,
        "notice_url": "https://inu.ac.kr/bbs/inu/246/385074/artclView.do"
    }
]
[
    {
        "category_id": "246",
        "title": "2024학년도 1학기 수강포기 처리완료 안내",
        "content": "\n2024학년도 1학기 수강포기 처리완료 안내\n2024학년도 1학기 수강포기 신청에 대한 처리결과를 아래와 같이 안내합니다.\n- 수강포기 결과 확인: 통합정보 → 학사행정 → 학적 → 학적기본관리 → 개인학적조회(학생)\n",
        "img_url": [],
        "posted_at": "2024.03.28",
        "deadline": "",
        "isclassified": false,
        "notice_url": "https://inu.ac.kr/bbs/inu/246/385042/artclView.do"
    }
]
 */
}
