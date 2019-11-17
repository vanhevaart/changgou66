package org.changgou.goods.service;

import com.github.pagehelper.PageInfo;
import org.changgou.goods.pojo.Album;

import java.util.List;

/**
 * 商品相册管理service接口
 */
public interface AlbumService {

    /**
     * 查询所有相册数据
     * @return 返回相册数据
     */
    List<Album> findAll();

    /**
     * 根据Id查询相册
     * @param id 相册id
     * @return 返回相册数据
     */
    Album findById(Long id);

    /**
     * 增加商品相册数据
     * @param album 封装了要新增的相册信息
     */
    void add(Album album);

    /**
     * 更新商品相册信息
     * @param album 封装了要更新的相册信息
     */
    void update(Album album);

    /**
     * 根据id删除商品相册数据
     * @param id 相册id
     */
    void deleteById(Long id);

    /**
     * 根据条件查询商品相册数据
     * @param album 封装了查询的条件
     * @return 返回符合条件的相册数据
     */
    List<Album> findByCondition(Album album);

    /**
     * 分页查询商品相册数据
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的相册数据
     */
    PageInfo<Album> findByPage(Integer pageNum, Integer size);

    /**
     * 分页+条件查询
     * @param album 封装了查询条件
     * @param pageNum 当前页码数
     * @param size 每页显示个数
     * @return 当前页面所需要的相册数据
     */
    PageInfo<Album> search(Album album, Integer pageNum, Integer size);
}
