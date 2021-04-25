package io.andrea.demorequestonhold.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RequestOnHoldRepository extends JpaRepository<RequestOnHold, String> {

    @Query("SELECT s FROM RequestOnHold s WHERE s.status = 'RECEIVED'")
    List<RequestOnHold> getReceivedRequest();

    @Modifying
    @Transactional
    @Query("DELETE FROM RequestOnHold s WHERE s.expireOn < :timestamp")
    void deleteSignalsExpiredBefore(@Param("timestamp") Timestamp timestamp);
}
