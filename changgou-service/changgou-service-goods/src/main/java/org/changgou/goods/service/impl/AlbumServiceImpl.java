package org.changgou.goods.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.AlbumMapper;
import org.changgou.goods.pojo.Album;
import org.changgou.goods.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Author:  HZ
 * <p> 商品相册管理service
 * Create:  2019/8/11  22:49
 */
@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired(required = false)
    private AlbumMapper albumMapper;

    /**
     * 查询所有商品相册数据
     *
     * @return 返回相册数据
     */
    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    /**
     * 根据Id查询相册
     *
     * @param id 相册id
     * @return 返回相册数据
     */
    @Override
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    /**
     * 增加商品相册数据
     *
     * @param album 封装了要新增的相册信息
     */
    @Override
    public void add(Album album) {
        // 保存一个实体，null的属性不会保存，会使用数据库默认值
        albumMapper.insertSelective(album);
    }

    /**
     * 更新商品相册信息
     *
     * @param album 封装了要更新的相册信息
     */
    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKeySelective(album);
    }

    /**
     * 根据id删除商品相册数据
     *
     * @param id 相册id
     */
    @Override
    public void deleteById(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据条件查询商品相册数据
     *
     * @param album 封装了查询的条件
     * @return 返回符合条件的相册数据
     */
    @Override
    public List<Album> findByCondition(Album album) {
        // 封装查询条件
        Example example = getExample(album);
        return albumMapper.selectByExample(example);
    }

    /**
     * 分页查询商品相册数据
     *
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的相册数据,
     */
    @Override
    public PageInfo<Album> findByPage(Integer pageNum, Integer size) {
        // 使用第三方插件进行分页查询
        PageHelper.startPage(pageNum, size);
        List<Album> albums = albumMapper.selectAll();
        // 将查询结果进行封装,并返回
        return new PageInfo<>(albums);
    }

    /**
     * 分页+条件查询
     *
     * @param album   封装了查询条件
     * @param pageNum 当前页码数
     * @param size    每页显示个数
     * @return 当前页面所需要的相册数据
     */
    @Override
    public PageInfo<Album> search(Album album, Integer pageNum, Integer size) {
        Example example = getExample(album);
        PageHelper.startPage(pageNum, size);
        List<Album> albums = albumMapper.selectByExample(example);
        return new PageInfo<>(albums);
    }

    /**
     * 封装查询条件的方法
     *
     * @param album 含有查询条件的实体类对象
     * @return 封装了查询条件的对象
     */
    private Example getExample(Album album) {
        // 封装查询条件对象 允许表的列不存在,也允许实体类的字段不存在
        Example example = new Example(Album.class, false, false);
        // Criteria为实际进行动态Sql语句拼接的对象
        Example.Criteria criteria = example.createCriteria();
        if(album != null) {
            criteria.andLike("title", "%" + (album.getTitle() == null ? "" : album.getTitle()) + "%")
                    .andEqualTo("image", album.getImage())
                    .andEqualTo("imageItems", album.getImageItems())
                    .andEqualTo("id", album.getId());
        }
        return example;
    }
}
