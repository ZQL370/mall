package com.imooc.mall.service.impl;

import com.imooc.mall.dao.CategoryMapper;
import com.imooc.mall.pojo.Category;
import com.imooc.mall.service.ICategoryService;
import com.imooc.mall.vo.CategoryVo;
import com.imooc.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.color.CMMException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.imooc.mall.consts.MallConst.ROOT_PARENT_ID;
/*
* 查询商品类目。先查出1级目录，再查其子目录，一直查到null
* */

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 耗时：http(请求微信api) > 磁盘 > 内存
     * mysql(内网+磁盘)
     * @return
     */
    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        //List<CategoryVo> categoryVoList = new ArrayList<>();
        List<Category> categories = categoryMapper.selectAll();
        //1.查出一级目录parent_id=0
        /*for (Category category : categories){
            if (category.getParentId().equals(ROOT_PARENT_ID)){
                CategoryVo categoryVo = new CategoryVo();
                BeanUtils.copyProperties( category, categoryVo );
                categoryVoList.add( categoryVo );
            }
        }*/
        //lambda+stream
        List<CategoryVo> categoryVoList =categories.stream()
                .filter( e->e.getParentId().equals( ROOT_PARENT_ID ) )
                .map( this:: category2CategoryVo)
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect( Collectors.toList());

        //2.查询子目录
        findSubCategory( categoryVoList,categories );

        return ResponseVo.success(categoryVoList);


    }
    private void findSubCategory(List<CategoryVo> categoryVoList,List<Category> categories){
        for (CategoryVo categoryVo : categoryVoList){
            List<CategoryVo> subCategoryVoList = new ArrayList<>( );
            for (Category category : categories){
                //如果查到子目录，就将其找出设置为subCategory，再继续往下查
                if (categoryVo.getId().equals( category.getParentId() )){
                    CategoryVo subCategoryVo = category2CategoryVo( category );
                    subCategoryVoList.add( subCategoryVo );
                }
                //将子目录按从低到高排序
                subCategoryVoList.sort( Comparator.comparing( CategoryVo::getSortOrder ).reversed() );
                categoryVo.setSubCategories( subCategoryVoList );
                //递归一直查完所有子目录
                findSubCategory( subCategoryVoList,categories );
            }
        }

    }

    private CategoryVo category2CategoryVo(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties( category, categoryVo );
        return categoryVo;
    }
}
