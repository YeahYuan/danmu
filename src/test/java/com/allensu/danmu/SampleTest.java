package com.allensu.danmu;

import com.allensu.danmu.entity.DanmuConfig;
import com.allensu.danmu.mapper.DanmuConfigMapper;
import com.allensu.danmu.mapper.DanmuTaskTransMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class SampleTest {
    @Autowired
    private DanmuConfigMapper danmuConfigMapper;
    @Autowired
    private DanmuTaskTransMapper danmuTaskTransMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<DanmuConfig> userList = danmuConfigMapper.selectList(null);
        userList.forEach(System.out::println);
        danmuTaskTransMapper.selectList(null);
    }
}
