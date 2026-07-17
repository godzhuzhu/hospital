package com.whlg.hospital.service;

import java.util.Map;

public interface SystemService {

    Map<String, Object> getConfig(String key);

    Map<String, Object> health();
}