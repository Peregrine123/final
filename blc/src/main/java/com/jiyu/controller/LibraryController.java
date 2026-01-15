package com.jiyu.controller;


import com.jiyu.pojo.Collection;
import com.jiyu.pojo.Movie;
import com.jiyu.service.CollectionService;
import com.jiyu.service.MovieService;
import com.jiyu.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class LibraryController {
    @Autowired
    MovieService movieService;
    @Autowired
    CollectionService collectionService;

    @Value("${blc.upload.dir:${user.dir}/uploads}")
    private String uploadDir;

    @GetMapping("/api/movies")
    public List<Movie> list() throws Exception {
        return movieService.list();
    }

    @PostMapping("/api/admin/movies")
    public Movie addOrUpdate(@RequestBody Movie movie) throws Exception {
        movieService.addOrUpdate(movie);
        return movie;
    }
    @GetMapping("/api/collections")
    public List<Collection> listCollection() throws Exception{
        return collectionService.list();
    }
    @PostMapping("/api/user/add")
    public Collection addOrUpdateCollection(@RequestBody Collection collection) throws Exception {
        collectionService.addOrUpdate(collection);
        return collection;
    }
    @PostMapping("/api/user/delete")
    public void delete(@RequestBody Collection collection) throws Exception {
        collectionService.deleteById(collection.getId());
    }

    @PostMapping("/api/admin/delete")
    public void delete(@RequestBody Movie movie) throws Exception {
        movieService.deleteById(movie.getId());
    }


    @GetMapping("/api/categories/{cid}/movies")
    public List<Movie> listByCategory(@PathVariable("cid") int cid) throws Exception {
        if (0 != cid) {
            return movieService.listByCategory(cid);//查询一类
        } else {
            return list();//查询所有
        }
    }
    @GetMapping("/api/categories/{cid}/collections")
    public List<Collection> listByCategoryCollection(@PathVariable("cid") int cid) throws Exception {
        if (0 != cid) {
            return collectionService.listByCategory(cid);//查询一类
        } else {
            return collectionService.list();//查询所有
        }
    }

    @GetMapping("/api/search")
    public List<Movie> searchResult(@RequestParam("keywords") String keywords) {
        if ("".equals(keywords)) {
            return movieService.list();
        } else {
            return movieService.Search(keywords);
        }
    }

    @PostMapping("api/covers")
    public String coversUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        System.out.println("文件上传中");
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // new Date()为获取当前系统时间
        String format = df.format(new Date());

        String original = file.getOriginalFilename();
        String suffix = "";
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0 && dot < original.length() - 1) {
                suffix = original.substring(dot);
            }
        }

        String filename = StringUtils.getRandomString(6) + "-" + format + suffix;
        try {
            Files.createDirectories(dir);
            File f = dir.resolve(filename).toFile();
            file.transferTo(f);
            // Return a path (not a fixed host/port) so the frontend can work behind proxies / different deployments.
            String ctx = request.getContextPath();
            if (ctx == null) ctx = "";
            return ctx + "/api/file/" + f.getName(); // served by MyWebConfigurer
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }

}
