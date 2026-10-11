
package com.agent.apidocassistant;


import com.agent.apidocassistant.ApiDocAssistantApplication;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiParserService {

    public List<ApiEndPoint> parse(String content) {

        ParseOptions options = new ParseOptions();
        SwaggerParseResult result =
                new OpenAPIV3Parser().readContents(
                        content, null, options
                );

        OpenAPI openAPI = result.getOpenAPI();

        if (openAPI == null || openAPI.getPaths() == null) {
            throw new IllegalArgumentException(
                    "Invalid or unsupported OpenAPI specification"
            );
        }

        List<ApiEndPoint> endpoints = new ArrayList<>();

        for (Map.Entry<String, PathItem> entry
                : openAPI.getPaths().entrySet()) {

            String path = entry.getKey();
            PathItem pathItem = entry.getValue();

            for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry
                    : pathItem.readOperationsMap().entrySet()) {

                String method = operationEntry.getKey().name();
                Operation operation = operationEntry.getValue();

                List<String> parameters = new ArrayList<>();

                if (operation.getParameters() != null) {
                    operation.getParameters().forEach(parameter ->
                            parameters.add(
                                    parameter.getName()
                                            + " (in: "
                                            + parameter.getIn()
                                            + ", required: "
                                            + parameter.getRequired()
                                            + ")"
                            )
                    );
                }

                String requestBody = null;

                if (operation.getRequestBody() != null
                        && operation.getRequestBody().getContent() != null) {
                    requestBody = String.join(
                            ", ",
                            operation.getRequestBody()
                                    .getContent().keySet()
                    );
                }

                Map<String, String> responses = new LinkedHashMap<>();

                if (operation.getResponses() != null) {
                    operation.getResponses().forEach(
                            (status, response) ->
                                    responses.put(
                                            status,
                                            response.getDescription()
                                    )
                    );
                }

                endpoints.add(new ApiEndPoint(
                        path,
                        method,
                        operation.getSummary(),
                        operation.getDescription(),
                        parameters,
                        requestBody,
                        responses
                ));
            }
        }

        return endpoints;
    }
}
