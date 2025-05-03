package challenge.tenpo.tenpo.repository;

import challenge.tenpo.tenpo.entity.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findAll(Pageable pageable);
}

