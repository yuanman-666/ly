package com.leyou.search.client;

import com.leyou.LySearchApplication;
import com.leyou.item.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class CategoryClientTest {

    @Autowired
    CategoryClient categoryClient;

    @Test
    public void testQueryCategories(){
        ResponseEntity<List<String>> category = categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L));
        category.getBody().forEach(System.out::println);
    }

}
