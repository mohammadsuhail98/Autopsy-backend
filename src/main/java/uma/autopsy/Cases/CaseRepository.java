package uma.autopsy.Cases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Integer>  {
    Optional<Case> findByName(String name);
    Optional<Case> findByNameAndDeviceId(String name, String deviceId);
    List<Case> findByDeviceId(String deviceId);
    Optional<Case> findByIdAndDeviceId(int id, String deviceId);
}
