package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.AiSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AiSettingsMapper extends BaseMapper<AiSettings> {

    @Select("SELECT setting_value FROM ai_settings WHERE setting_key = #{key}")
    String getValueByKey(@Param("key") String key);

    @Update("UPDATE ai_settings SET setting_value = #{value} WHERE setting_key = #{key}")
    int updateValueByKey(@Param("key") String key, @Param("value") String value);
}
