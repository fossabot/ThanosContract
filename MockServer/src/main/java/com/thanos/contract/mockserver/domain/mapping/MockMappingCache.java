package com.thanos.contract.mockserver.domain.mapping;

import com.thanos.contract.mockserver.exception.BizException;

import java.util.HashMap;
import java.util.Map;

public class MockMappingCache {

    private static Map<String, Integer> mockMappings = new HashMap<>();

    static synchronized void addNewMockMapping(MockMapping mockMapping) {
        if (mockMappings.containsKey(mockMapping.getIndex()))
            throw new BizException("MockServer already existed at" +
                    mockMapping.getIndex() + "/" + mockMappings.get(mockMapping.getIndex()));
        else
            mockMappings.put(mockMapping.getIndex(), mockMapping.getPort());
    }

    static synchronized Map<String, Integer> getAllMockMapping() {
        return mockMappings;
    }

    static synchronized void cleanupMockMapping() {
        mockMappings = new HashMap<>();
    }

}
