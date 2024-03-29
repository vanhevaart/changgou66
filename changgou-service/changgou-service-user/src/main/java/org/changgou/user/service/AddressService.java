package org.changgou.user.service;

import com.github.pagehelper.PageInfo;
import org.changgou.user.pojo.Address;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Address业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface AddressService {

    /***
     * Address多条件分页查询
     * @param address
     * @param page
     * @param size
     * @return
     */
    PageInfo<Address> findPage(Address address, int page, int size);

    /***
     * Address分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Address> findPage(int page, int size);

    /***
     * Address多条件搜索方法
     * @param address
     * @return
     */
    List<Address> findList(Address address);

    /***
     * 删除Address
     * @param id
     */
    void delete(Integer id);

    /***
     * 修改Address数据
     * @param address
     */
    void update(Address address);

    /***
     * 新增Address
     * @param address
     */
    void add(Address address);

    /**
     * 根据ID查询Address
     * @param id
     * @return
     */
     Address findById(Integer id);

    /***
     * 查询所有Address
     * @return
     */
    List<Address> findAll();

    /**
     * 根据用户名查询收货地址
     * @param username
     * @return
     */
    List<Address> findByUsername(String username);
}
