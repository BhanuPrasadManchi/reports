package com.RTDMPL.thymeleaf;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.DocumentException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.*;
import java.net.MalformedURLException;
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
    public String generatePdf(Model model) throws IOException {
        Context context = new Context();
        String html = templateEngine.process("test_dom", context);
        System.out.println(html);
//        HtmlConverter.convertToPdf(html, new FileOutputStream("src/main/resources/report.pdf"));
//        try (final WebClient webClient = new WebClient()) {
//            final HtmlPage page = webClient.getPage("https://msaikrishna.github.io/report-generation/");
//            webClient.waitForBackgroundJavaScript(60_000);
//            String domhtml = page.getWebResponse().getContentAsString();
//            System.out.println("----DomHtml-----");
//            System.out.println(domhtml);
//            System.out.println("----DomHtmlEnd-----");
//        } catch (Exception e) {
//        }
//        System.out.println("-----SeleniumIn----");
//        WebDriver driver;
//        ChromeOptions options=new ChromeOptions();
//        options.addArguments("headless");
//        driver=new ChromeDriver(options);
//        driver.get("https://msaikrishna.github.io/report-generation/");
//        String pageSource = driver.getPageSource();
//        System.out.println(pageSource);
//
//        String filePath = "src/main/resources/dom_page_source.html";
//
//        try {
//            File file = new File(filePath);
//            FileWriter fileWriter = new FileWriter(file);
//            fileWriter.write(pageSource);
//            fileWriter.flush();
//            fileWriter.close();
//            System.out.println("HTML file written successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//            ITextRenderer renderer = new ITextRenderer();
//            renderer.setDocumentFromString(pageSource);
//            renderer.layout();
//            renderer.createPDF(new FileOutputStream("src/main/resources/report.pdf"), false);
//            renderer.finishPDF();
//
//            Document document = Jsoup.parse(pageSource, "UTF-8");
//            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withUri("src/main/resources/report_open.pdf");
//            builder.toStream(new FileOutputStream("src/main/resources/report_open.pdf"));
//            builder.withW3cDocument(new W3CDom().fromJsoup(document), "/");
//            builder.run();

        //Request from online data
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("url", "https://msaikrishna.github.io/report-generation")
//                .build();
//            Request request = new Request.Builder()
//                    .url("http://localhost:12/forms/chromium/convert/url")
//                    .post(requestBody)
//                    .build();
//         Response response = client.newCall(request).execute();
//        InputStream pdfData = response.body().byteStream();
//        try(FileOutputStream outputStream = new FileOutputStream("src/main/resources/report_from_gotenberg.pdf")){
//            IOUtils.copy(pdfData, outputStream);
//        } catch (FileNotFoundException e) {
//            // handle exception here
//        } catch (IOException e) {
//            // handle exception here
//        }

        //Request from html data
        OkHttpClient client = new OkHttpClient();
        File testHtmlfile = new File("src/main/resources/templates/index.html");
        File testJsfile = new File("src/main/resources/static/graph.js");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", testHtmlfile.getName(), RequestBody.create(MediaType.parse("text/plain"), testHtmlfile))
                .addFormDataPart("files", testJsfile.getName(), RequestBody.create(MediaType.parse("text/plain"), testJsfile))
                .addFormDataPart("paperWidth", "21cm")
                .addFormDataPart("paperHeight", "29.7cm")
                .addFormDataPart("preferCssPageSize", "0")
                .addFormDataPart("marginTop", "0")
                .addFormDataPart("marginBottom", "0")
                .addFormDataPart("marginLeft", "0")
                .addFormDataPart("marginRight", "0")
                .build();
        Request request = new Request.Builder()
                .url("http://localhost:12/forms/chromium/convert/html")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        InputStream pdfData = response.body().byteStream();
        try(FileOutputStream outputStream = new FileOutputStream("src/main/resources/report_from_gotenberg_local.pdf")){
            IOUtils.copy(pdfData, outputStream);
        } catch (FileNotFoundException e) {
            // handle exception here
        } catch (IOException e) {
            // handle exception here
        }
        System.out.println("-----SeleniumOut----");
//        driver.quit();
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
