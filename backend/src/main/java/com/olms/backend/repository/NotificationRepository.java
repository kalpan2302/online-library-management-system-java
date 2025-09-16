// com/olms/backend/repository/NotificationRepository.java
package com.olms.backend.repository;

import com.olms.backend.entities.Notification;
import com.olms.backend.entities.NotificationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("""
     select count(n) > 0 from Notification n
     where n.userId = :uid
       and (:loanId is null or n.loanId = :loanId)
       and n.type = :type
       and n.sentAt > :since
  """)
  boolean existsRecent(@Param("uid") Long userId,
                       @Param("loanId") Long loanId,
                       @Param("type") NotificationType type,
                       @Param("since") Instant since);

  List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}
