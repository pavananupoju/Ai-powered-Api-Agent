package com.agent.apidocassistant;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/specifications")
public class OpenApiController {

    private final OpenApiParserService parserService;

    public OpenApiController(OpenApiParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping(
            value = "/parse",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ApiEndPoint> parseSpecification(
            @RequestBody String content) {

        return parserService.parse(content);
    }
}