package engine.presentation;

import engine.business.*;
import engine.exception.QuizNotFoundException;
import engine.persisence.AppUserAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<Page<Quiz>> getQuiz(@RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok(quizService.getQuizzes(page));
    }

    @GetMapping(path = "/completed")
    public ResponseEntity<Page<CompletedDTO>> getCompletedQuiz(@RequestParam(defaultValue = "0") Integer page, @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        return ResponseEntity.ok(quizService.getCompletedQuizzes(page, appUserAdapter.appUser()));
    }

    @PostMapping
    public ResponseEntity<Quiz> addQuiz(@Valid @RequestBody Quiz quiz, @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        quiz.setAppUser(appUserAdapter.appUser());
        return ResponseEntity.ok(quizService.addQuiz(quiz));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(quizService.getQuizById(id));
        } catch (QuizNotFoundException quizNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteQuizById(@PathVariable Long id, @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        try {
            quizService.deleteById(id, appUserAdapter.appUser().getId());
            return ResponseEntity.noContent().build();
        } catch (QuizNotFoundException quizNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException accessDeniedException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(path = "/{id}/solve")
    public ResponseEntity<AnswerResponse> solveQuizById(@PathVariable Long id, @RequestBody Answer answer, @AuthenticationPrincipal AppUserAdapter appUserAdapter) {
        try {
            Quiz quiz = quizService.getQuizById(id);
            boolean correctAnswer = isCorrectAnswer(quiz, answer);
            if (correctAnswer) {
                Completed completed = new Completed();
                completed.setQuiz(quiz);
                completed.setCompletedAt(LocalDateTime.now());
                completed.setAppUser(appUserAdapter.appUser());
                quizService.saveCompleted(completed);
            }
            return ResponseEntity.ok(new AnswerResponse(correctAnswer, correctAnswer ? "Congratulations, you're right!" : "Wrong answer! Please, try again."));
        } catch (QuizNotFoundException quizNotFoundException) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isCorrectAnswer(Quiz quiz, Answer answer) {
        List<Integer> correctAnswers = quiz.getAnswer();
        List<Integer> providedAnswers = answer.answer();

        if (correctAnswers == null) {
            return providedAnswers == null || providedAnswers.isEmpty();
        }

        return providedAnswers != null
                && correctAnswers.size() == providedAnswers.size()
                && new HashSet<>(correctAnswers).containsAll(providedAnswers);
    }
}
