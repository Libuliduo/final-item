//package top.yeyuchun.controller;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import top.yeyuchun.entity.Movie;
//import top.yeyuchun.template.UserBasedCollaborativeFiltering;
//
//import javax.servlet.http.HttpSession;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("movie")
//public class RecommendController {
//
//    @PostMapping("recommend")
//    public List<Movie> recommend(@RequestBody Map<String,String> map, HttpSession session) {
//        String uid = map.get("uid").toString();
//        if (uid != null && !uid.equals("")) {
//            User user = userService.queryById(Integer.parseInt(uid));
//            if (user != null) {
//                List<Movie>
//            }
//        }
//
//        // 创建协同过滤对象
//        UserBasedCollaborativeFiltering filter = new UserBasedCollaborativeFiltering();
//        // 为指定用户推荐影视
//        String targetUser = user.getId().toString();
//        int numRecommendations = 8;
//        List<Movie> result = new ArrayList<>();
//        List<String> recommendations = filter.recommendItems(targetUser,numRecommendations);
//        if(recommendations.size() > 0) {
//            for (String item:recommendations) {
//                Movie movie = movieService.queryById(Integer.valueOf(item));
//                result.add(movie);
//            }
//            return result;
//        }
//    }
//}
