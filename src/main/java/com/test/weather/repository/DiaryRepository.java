package com.test.weather.repository;

import com.test.weather.domain.Diary;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository  extends JpaRepository<Diary,Integer>{

    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    Diary getFirstByDate(LocalDate date);

    @Transactional // delete의 경우 repository 작성시 해당 어노테이션을 꼭 붙여주어야만 기능이 제대로 수행된다
    void deleteAllByDate(LocalDate date);

}
