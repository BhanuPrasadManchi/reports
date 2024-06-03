package com.RTDMPL.thymeleaf;

import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.DocumentException;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;


@Controller
public class ReportsController {

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    RenderGraphService renderGraphService;

    @GetMapping("/report")
    public String generatePdf(Model model) throws FileNotFoundException {
        Context context = new Context();
        String html = templateEngine.process("test", context);
        HtmlConverter.convertToPdf(html, new FileOutputStream("src/main/resources/report.pdf"));
        return "test";
    }

    @GetMapping("/thymeleaf")
    public String getThymeleafView(Model model){
        /*PageMetricsDTO pageMetricsByChannelId = pageMetricService.getPageMetricsByChannelId(2L, 7);
        Gson gson = new Gson();
        Map<String,Object> map = new HashMap<>();
        map.put("data",gson.toJson(pageMetricsByChannelId));
        model.addAttribute("data",gson.toJson(pageMetricsByChannelId));*/
//        modelAndView.setViewName("/thymeleaf/test");
//        String s3Url = s3StorageService.getS3Url(generatePdg(map), "charts.pdf");
//        System.out.println(s3Url);

        Gson gson = new Gson();
        Resource resource = new ClassPathResource("/static/reports.json");
        FileOutputStream outputStream = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object object = mapper.readValue(resource.getInputStream(), Object.class);
            model.addAttribute("data",gson.toJson(object));

            Context context = new Context();
//            context.setVariable("data", gson.toJson(object));
            String html = templateEngine.process("test", context);
            outputStream = new FileOutputStream("src/main/resources/report.pdf");


            /*context.setVariable("name","Bhanu Prasad");
            context.setVariable("age","30");
            context.setVariable("country","India");
            String html = templateEngine.process("pdf-template", context);
            outputStream = new FileOutputStream("src/main/resources/test.pdf");*/

//            String xHtml = xhtmlConvert(html);

            renderGraphService.renderGraphs();

//            runNodeScript();

            ITextRenderer renderer = new ITextRenderer();

            String baseUrl = FileSystems
                    .getDefault()
                    .getPath("src", "main", "resources","templates")
                    .toUri()
                    .toURL()
                    .toString();
            renderer.setDocumentFromString(html, baseUrl);
            renderer.layout();

//            outputStream = new FileOutputStream("src/main/resources/test.pdf");
            renderer.createPDF(outputStream);


            /*final File outputFile = File.createTempFile("thymeleaf", ".pdf");
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();*/

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
        return "test";
    }

    private String xhtmlConvert(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString("UTF-8");
    }

    private void runNodeScript() {
        try {
            ProcessBuilder pb = new ProcessBuilder("node", "/Users/bhanuprasad/Downloads/thymeleaf/src/main/resources/static/graph.js");
            pb.inheritIO(); // To show the Node.js output in the console
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/json")
    @ResponseBody
    public String sampleJson(){
        Gson gson = new Gson();
        Resource resource = new ClassPathResource("/static/reports.json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object object = mapper.readValue(resource.getInputStream(), Object.class);
            return gson.toJson(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
