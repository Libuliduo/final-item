package top.yeyuchun.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import top.yeyuchun.entity.Movie;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 增加/修改单条数据测试
    @Test
    public void testSaveOrUpdate() throws Exception {
        Movie movie = new Movie();
//        movie.setId(3);
        movie.setTitle("星际穿越4");
        movie.setOriginalTitle("Interstellar");
        movie.setGenres(Arrays.asList("科幻"));
        movie.setCountry("US");
        movie.setReleaseDate(Date.valueOf("2014-11-05"));
        movie.setOverview("未来地球即将毁灭...");
        movie.setDirector("克里斯托弗·诺兰");
        movie.setActors("马修·麦康纳, 安妮·海瑟薇");
        movie.setBackdropPath("https://image.tmdb.org/t/p/w500/8sNiAPPYU14PUepFNeSNGUTiHW.jpg");
        movie.setPosterPath("https://image.tmdb.org/t/p/w500/v7Iib57HHgSEI9B7XYRF6qrs11T.jpg");
        movie.setUrl("");
        movie.setPopularity(8.9);

        String json = objectMapper.writeValueAsString(movie);

        mockMvc.perform(post("/movie/saveOrUpdate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    // 删除单条记录测试
    @Test
    public void testDeleteById() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                .delete("/movie/deleteById")
                .param("id","3")
        )
                .andExpect(status().isOk());
    }

    // 批量删除测试
    @Test
    public void testDeleteBatch() throws Exception {
        Integer[] ids = {4,5,6};

        mockMvc.perform(delete("/movie/deleteByIds")
                .param("ids", String.valueOf(ids[0]))
                .param("ids", String.valueOf(ids[1]))
                .param("ids", String.valueOf(ids[2]))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());  // 期望返回200状态码
    }
}
