package com.test.weather;

import com.test.weather.domain.Memo;
import com.test.weather.repository.JdbcMemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional // 실제 DB에 반영하지 않게 해줌
public class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest(){

        // given
        Memo newMemo = new Memo(2, "insertMemoTest");

        // when
        jdbcMemoRepository.save(newMemo); // * save()

        // then
        Optional<Memo> result = jdbcMemoRepository.findById(2); // * findById()
        assertEquals(result.get().getText(), "insertMemoTest");

    }

    @Test
    void findAllMemoTest(){
        List<Memo> memoList = jdbcMemoRepository.findAll(); // * findAll()
        System.out.println(memoList);
        assertNotNull(memoList);
    }

}
