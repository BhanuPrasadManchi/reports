package com.RTDMPL.thymeleaf;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class RenderGraphService {

    public void renderGraphs() throws IOException {

        Context context = Context.create();

        // Load the AnyChart library and your custom JavaScript
        String anyChartLib = new String(Files.readAllBytes(Paths.get("/Users/bhanuprasad/Downloads/thymeleaf/src/main/resources/static/anychart.js")));
        String customJs = new String(Files.readAllBytes(Paths.get("/Users/bhanuprasad/Downloads/thymeleaf/src/main/resources/static/graph.js")));

        // Evaluate the JavaScript in the context
        context.eval("js", anyChartLib);
        context.eval("js", customJs);

        // Execute the JavaScript to render graphs
        Value renderGraphs = context.eval("js", "renderGraphs();");

        // You can interact with the JavaScript result here if needed
        System.out.println(renderGraphs.toString());
    }
}
