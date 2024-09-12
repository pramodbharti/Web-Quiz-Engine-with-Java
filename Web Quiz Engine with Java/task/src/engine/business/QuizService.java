package engine.business;

import engine.exception.QuizNotFoundException;
import engine.persisence.CompletedRepository;
import engine.persisence.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final CompletedRepository completedRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, CompletedRepository completedRepository) {
        this.quizRepository = quizRepository;
        this.completedRepository = completedRepository;
    }

    public Page<Quiz> getQuizzes(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        return quizRepository.findAll(pageable);
    }

    public Quiz addQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void saveCompleted(Completed completed) {
        completedRepository.save(completed);
    }

    public Quiz getQuizById(Long id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isPresent()) return quiz.get();
        throw new QuizNotFoundException();
    }

    @Transactional
    public void deleteById(Long id, Integer userId) throws AccessDeniedException {
        var quiz = quizRepository.findById(id).orElseThrow(QuizNotFoundException::new);
        if (!quiz.getAppUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        completedRepository.deleteByQuizId(quiz.getId());
        quizRepository.deleteById(id);
    }

    public Page<CompletedDTO> getCompletedQuizzes(Integer page, AppUser appUser) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("completedAt").descending());
        return completedRepository.findAllByAppUserId(appUser.getId(), pageable).map(completed -> new CompletedDTO(completed.getQuiz().getId(), completed.getCompletedAt()));
    }
}
