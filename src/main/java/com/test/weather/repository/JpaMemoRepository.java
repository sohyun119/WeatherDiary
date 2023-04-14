package com.test.weather.repository;

import com.test.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {
    // 이렇게 JPA 방식은 findAll, save 등을 따로 지정해주지 않아도 간단히 사용 가능!!



}
