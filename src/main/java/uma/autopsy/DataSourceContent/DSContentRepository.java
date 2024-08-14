package uma.autopsy.DataSourceContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uma.autopsy.DataSource.DataSource;

import java.util.List;
import java.util.Optional;

@Repository
public interface DSContentRepository extends JpaRepository<DataSource, Integer> {

}