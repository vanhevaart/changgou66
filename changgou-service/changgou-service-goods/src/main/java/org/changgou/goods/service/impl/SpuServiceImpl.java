package org.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changgou.goods.dao.BrandMapper;
import org.changgou.goods.dao.CategoryMapper;
import org.changgou.goods.dao.SkuMapper;
import org.changgou.goods.dao.SpuMapper;
import org.changgou.goods.pojo.Goods;
import org.changgou.goods.pojo.Sku;
import org.changgou.goods.pojo.Spu;
import org.changgou.goods.service.SpuService;
import org.changgou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired(required = false)
    private SpuMapper spuMapper;
    @Autowired(required = false)
    private SkuMapper skuMapper;
    @Autowired(required = false)
    private CategoryMapper categoryMapper;
    @Autowired(required = false)
    private BrandMapper brandMapper;
    @Autowired
    private IdWorker idWorker;

    /**
     * 逻辑性的改变商品状态(改变是否删除字段的值)
     *
     * @param id 商品id
     */
    @Override
    public void logicDelete(Long id, String isDelete) {
        // 要删除商品,该商品必须是下架的状态
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if("1".equals(spu.getIsMarketable())) { // 上架的状态不能删除
            throw new RuntimeException("商品上架中,不能删除,请先下架该商品");
        }
        spu.setIsDelete(isDelete);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 批量上下架
     *
     * @param ids          需要进行操作的id的集合
     * @param isMarketable 上/下架标志
     */
    @Override
    public int isShows(Long[] ids, String isMarketable) {
        // 需要进行是否审核通过,是否是删除的商品等判断,通过构造example对象
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 构建条件对象
        criteria.andIn("id", Arrays.asList(ids));// 在一定范围内
        criteria.andEqualTo("status", "1"); // 审核通过的
        criteria.andEqualTo("isDelete", 0); // 未删除的
        // 更新状态
        Spu spu = new Spu();
        spu.setIsMarketable(isMarketable);
        return spuMapper.updateByExampleSelective(spu, example);
    }

    /**
     * 根据id上/下架商品
     *
     * @param id 商品id
     */
    @Override
    public void isShow(Long id, String isMarketable) {
        // 上架商品需要通过审核并且不能是已删除的商品
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(!"1".equals(spu.getStatus()) || "1".equals(spu.getIsDelete())) { // 未通过审核或者已经删除,不允许操作
            throw new RuntimeException("审核未通过或此商品已经删除,操作失败");
        }
        spu.setIsMarketable(isMarketable);
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 根据id改变商品的审核状态,并实现自动上架
     *
     * @param id 商品id
     */
    @Override
    public void audit(Long id) {
        // 需要判断商品是否是删除商品,如果是则不改变其审核状态
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(!"1".equals(spu.getIsDelete())) { // 商品未删除,进行审核状态变更,并自动上架
            spu.setStatus("1");
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        } else {
            throw new RuntimeException("商品已经删除,改变审核状态失败");
        }
    }

    /**
     * 根据id查询商品信息(包括spu和sku信息)
     *
     * @param spuId 商品spuId
     * @return 商品信息
     */
    @Override
    public Goods findGoodsById(Long spuId) {
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        // 封装数据
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 保存商品,分别进行SPU和SKU的保存
     *
     * @param goods 包含了SPU和SKU信息的对象
     */
    @Override
    public void saveGoods(Goods goods) {
        //1. 完善SPU的信息并进行保存
        Spu spu = goods.getSpu();
        // 判断id是否存在,来确定是更新操作还是新增操作
        Long id = spu.getId();
        if(id == null) { // id为空,为新增操作
            // 使用IDWork添加主键id
            spu.setId(idWorker.nextId());
            spu.setIsMarketable("0");//默认未上架
            spu.setIsEnableSpec("1");//默认启用规格
            spu.setIsDelete("0");//默认未删除
            spu.setStatus("0");//默认未审核
            spuMapper.insertSelective(spu);
        } else { // id不为空,为更新操作
            // 商品更新后,需要重新审核
            spu.setStatus("0");
            spuMapper.updateByPrimaryKeySelective(spu);
            // 删除相关联的所有sku数据
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
        }
        //2. 完善SKU的信息并进行保存
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            // 使用IDWork添加主键id
            sku.setId(idWorker.nextId());
            StringBuilder name = new StringBuilder(spu.getName() + " " + spu.getCaption() + " ");//初步将spu和名字和副标题进行拼接
            // 拼接name开始
            String spec = sku.getSpec();//取出规格的json串
            Map map = JSON.parseObject(spec, Map.class);// 使用IDWork添加主键id
            if(map != null) {
                Collection values = map.values();
                for (Object value : values) {
                    // 循环拼接规格数据
                    name.append(value).append(" ");
                }
            }
            sku.setName(name.toString());
            // 拼接name结束
            // 完善其他信息
            sku.setCreateTime(new Date());// 设置创建时间
            sku.setUpdateTime(new Date());// 设置更新时间
            sku.setSpuId(spu.getId());// 设置spuId
            sku.setCategoryId(spu.getCategory3Id());// 设置分类id
            sku.setCategoryName(categoryMapper.selectByPrimaryKey(sku.getCategoryId()).getName());// 设置分类名称
            sku.setBrandName(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());// 设置品牌名称
            sku.setStatus("1");// 默认1
            skuMapper.insertSelective(sku);
        }
    }

    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(spu != null) {
            // 主键
            if(!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if(!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if(!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if(!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if(!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if(!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if(!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if(!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if(!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if(!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if(!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if(!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if(!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if(!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if(!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if(!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if(!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if(!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架
            if(!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if(!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除
            if(!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态
            if(!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 正在删除数据,需要将sku数据也一并删除
     *
     * @param id 商品id
     */
    @Override
    public void delete(Long id) {
        // 需要判断,要删除商品,该商品必须处于逻辑删除状态
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if("1".equals(spu.getIsDelete())) { // 处于逻辑删除状态
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(sku);
            spuMapper.deleteByPrimaryKey(id);
        } else {
            throw new RuntimeException("删除商品失败");
        }
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
