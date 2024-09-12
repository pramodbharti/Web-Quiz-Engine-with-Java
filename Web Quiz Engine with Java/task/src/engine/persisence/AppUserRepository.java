package engine.persisence;

import engine.business.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    @Transactional
    Optional<AppUser> findAppUserByEmail(String email);
}
