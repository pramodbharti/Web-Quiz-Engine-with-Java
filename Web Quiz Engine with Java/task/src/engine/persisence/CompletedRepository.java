package engine.persisence;

import engine.business.Completed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedRepository extends JpaRepository<Completed, Long> {
    Page<Completed> findAllByAppUserId(Integer appUserId, Pageable pageable);
    void deleteByQuizId(Long quizId);
}
