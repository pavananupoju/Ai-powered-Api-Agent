package com.agent.apidocassistant;

import java.util.List;
import java.util.Map;

public  record ApiEndPoint(
        String path,
        String method,
        String summary,
        String description,
        List<String> parameters,
        String requestBody,
        Map<String, String> responses
) {
}
