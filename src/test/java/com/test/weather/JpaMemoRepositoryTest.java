package com.test.weather;

import com.test.weather.domain.Memo;
import com.test.weather.repository.JpaMemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest(){
        // given
        Memo newMemo = new Memo(10, "this is jpa memo");

        // when
        jpaMemoRepository.save(newMemo);

        // then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);

    }

    @Test
    void findByIdTest(){
        //given
        Memo newMemo = new Memo(11, "jpa");  // 이렇게 id를 넣어도 mysql에서 autoIncrement로 설정하였기에 소용이없다.

        //when
        jpaMemoRepository.save(newMemo);

        //then
        Optional<Memo> result = jpaMemoRepository.findById(11);
        assertEquals(result.get().getText(), "jpa");
    }



}
