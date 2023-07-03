package com.fc.sns.repository;

import com.fc.sns.model.entity.Alarm;
import com.fc.sns.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Page<Alarm> findAllByUser(User user, Pageable pageable);
}
