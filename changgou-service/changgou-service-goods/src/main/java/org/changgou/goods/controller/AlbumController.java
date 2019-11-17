package org.changgou.goods.controller;

import com.github.pagehelper.PageInfo;
import org.changgou.entity.Result;
import org.changgou.entity.StatusCode;
import org.changgou.goods.pojo.Album;
import org.changgou.goods.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品相册管理Controller
 * Create:  2019/8/10  11:19
 */
@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 查询所有商品相册数据
     *
     * @return 返回相册数据
     */
    @GetMapping
    public Result<List<Album>> findAll() {
        List<Album> all = albumService.findAll();
        return new Result<>(true, StatusCode.OK, "查询成功", all);
    }

    /**
     * 根据Id查询相册
     *
     * @param id 相册id
     * @return 返回相册数据
     */
    @GetMapping("/{id}")
    public Result<Album> findById(@PathVariable Long id) {
        Album album = albumService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", album);
    }

    /**
     * 增加商品相册数据
     *
     * @param album 封装了要新增的相册信息
     */
    @PostMapping
    public Result save(@RequestBody Album album) {
        albumService.add(album);
        return new Result<>(true, StatusCode.OK, "增加成功");
    }

    /**
     * 更新商品相册信息
     *
     * @param album 封装了要更新的相册信息
     * @param id    要更新的相册的主键id
     */
    @PutMapping("/{id}")
    public Result update(@RequestBody Album album, @PathVariable Long id) {
        album.setId(id);
        albumService.update(album);
        return new Result<>(true, StatusCode.OK, "修改成功");
    }

    /**
     * 根据id删除商品相册数据
     *
     * @param id 相册id
     */
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Long id) {
        albumService.deleteById(id);
        return new Result<>(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据条件查询商品相册数据
     *
     * @param album 封装了查询的条件
     * @return 返回符合条件的相册数据
     */
    @PostMapping("/search")
    public Result<List<Album>> findByCondition(@RequestBody Album album) {
        List<Album> byCondition = albumService.findByCondition(album);
        return new Result<>(true, StatusCode.OK, "查询成功", byCondition);
    }

    /**
     * 分页查询商品相册数据
     *
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的相册数据,
     */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> findByPage(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Album> byPage = albumService.findByPage(page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }

    /**
     * 分页+条件查询
     *
     * @param album   封装了查询条件
     * @param page 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的相册数据
     */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> search(@RequestBody Album album, @PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<Album> byPage = albumService.search(album, page, size);
        return new Result<>(true, StatusCode.OK, "查询成功", byPage);
    }
}

