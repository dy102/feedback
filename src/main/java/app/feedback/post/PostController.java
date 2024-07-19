package app.feedback.post;

import app.feedback.auth.AuthService;
import app.feedback.auth.Authorized;
import app.feedback.auth.dto.Authentication;
import app.feedback.post.dto.PostCalendarResponse;
import app.feedback.post.dto.PostCreateUpdateRequest;
import app.feedback.post.dto.PostResponse;
import app.feedback.post.dto.PostsResponse;
import app.feedback.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final AuthService authService;

    @PostMapping
    public String create(final @Authorized Authentication authentication,
                         final @ModelAttribute PostCreateUpdateRequest request,
                         @RequestPart(name = "file") MultipartFile multipartFile,
                         final RedirectAttributes redirectAttributes) {
        authService.validateUser(authentication);
        Long postId = postService.create(authentication.email(), request, multipartFile);
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }

    @GetMapping("/{postId}")
    public String find(final @Authorized Authentication authentication,
                       final @PathVariable Long postId,
                       final Model model) {
        PostResponse response = postService.find(postId);
        model.addAttribute("response", response);
        model.addAttribute("auth", authentication);
        return "form/post";
    }

    @GetMapping
    public String read(final @Authorized Authentication authentication,
                       final Model model) {
        PostsResponse response = postService.read();
        model.addAttribute("response", response);
        model.addAttribute("auth", authentication);
        return "form/posts";
    }

    @GetMapping("/{memberId}/{date}")
    public String readMyPost(final @Authorized Authentication authentication,
                             final @PathVariable String memberId,
                             final @PathVariable String date,
                             final Model model) {
        authService.validateUser(authentication);
        PostsResponse response = postService.readMyPost(memberId, date);
        model.addAttribute("response", response);
        model.addAttribute("auth", authentication);
        return "form/posts";
    }

    @GetMapping("/new")
    public String postForm(final @Authorized Authentication authentication,
                           final Model model) {
        authService.validateUser(authentication);
        model.addAttribute("auth", authentication);
        return "form/postForm";
    }

    @GetMapping("/calendar/{memberId}")
    public String getCalendar(final @Authorized Authentication authentication,
                              final @PathVariable String memberId,
                              final @RequestParam(required = false) Integer year,
                              final @RequestParam(required = false) Integer month,
                              final Model model) {
        authService.validateUser(authentication);
        PostCalendarResponse calendar = postService.getCalendar(memberId, year, month);
        List<Integer> years = postService.getYears();
        List<Integer> months = postService.getMonths();
        model.addAttribute("response", calendar);
        model.addAttribute("auth", authentication);
        model.addAttribute("memberId", memberId);
        model.addAttribute("years", years);
        model.addAttribute("months", months);
        return "form/calendar";
    }
}