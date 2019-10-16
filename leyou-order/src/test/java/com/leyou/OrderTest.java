package com.leyou;

import com.leyou.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LyOrderApplication.class})
public class OrderTest {

    @Autowired
    private IdWorker idWorker;


    @Test
    public void TestIdWorker(){
        long nextId = idWorker.nextId();
        System.out.println(nextId);
    }
}
