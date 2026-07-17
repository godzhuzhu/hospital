package com.whlg.hospital.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whlg.hospital.entity.Config;
import com.whlg.hospital.mapper.ConfigMapper;
import com.whlg.hospital.service.SystemService;
import com.whlg.hospital.support.ApiException;
import com.whlg.hospital.util.StatusCode;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SystemServiceImpl extends ServiceSupport implements SystemService {

    private final ConfigMapper configMapper;
    private final ObjectMapper objectMapper;

    public SystemServiceImpl(ConfigMapper configMapper, ObjectMapper objectMapper) {
        this.configMapper = configMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> getConfig(String key) {
        Config config = configMapper.selectOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, key));
        if (config == null) {
            throw new ApiException(StatusCode.NOT_FOUND, "配置不存在");
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("key", config.getConfigKey());
        result.put("description", config.getDescription());
        try {
            Object value = objectMapper.readValue(config.getConfigValue(), new TypeReference<Object>() {
            });
            result.put("value", value);
        } catch (Exception ex) {
            result.put("value", config.getConfigValue());
        }
        return result;
    }

    @Override
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("status", "UP");
        result.put("service", "hospital-backend");
        return result;
    }
}