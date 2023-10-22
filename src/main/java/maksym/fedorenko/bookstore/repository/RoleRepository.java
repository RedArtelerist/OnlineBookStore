package maksym.fedorenko.bookstore.repository;

import maksym.fedorenko.bookstore.model.Role;
import maksym.fedorenko.bookstore.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
