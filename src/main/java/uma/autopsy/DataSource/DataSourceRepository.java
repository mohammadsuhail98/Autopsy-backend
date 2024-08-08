package uma.autopsy.DataSource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Integer> {
    List<DataSource> findByCaseEntity_Id(int caseId);
    Optional<DataSource> findByIdAndCaseEntity_Id(int id, int caseId);}
