package apiservices;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ZyteApiClient {
    private final String apiKey;
    private static final String ZYTE_API_URL = "https://api.zyte.com/v1/extract";

    public ZyteApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Scrape a URL and return the article text/content
     * @param url The URL to scrape
     * @return Scraped text content
     */
    public String scrapeUrl(String url) throws Exception {
        URL apiUrl = new URL(ZYTE_API_URL);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        // Zyte uses Basic Auth with API key as username
        String auth = apiKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setDoOutput(true);

        // Build request body - request browser rendering for JavaScript-heavy sites
        JSONObject requestBody = new JSONObject();
        requestBody.put("url", url);
        requestBody.put("browserHtml", true);  // Use browser rendering (better for modern sites)

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
            );
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            conn.disconnect();
            throw new Exception("Zyte API error (" + responseCode + "): " + errorResponse.toString());
        }

        // Read response
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        br.close();
        conn.disconnect();

        // Parse response
        JSONObject jsonResponse = new JSONObject(response.toString());

        String htmlBody = null;

        // Try browserHtml first (cleaner)
        if (jsonResponse.has("browserHtml")) {
            htmlBody = jsonResponse.getString("browserHtml");
        }
        // Fall back to httpResponseBody
        else if (jsonResponse.has("httpResponseBody")) {
            String base64Html = jsonResponse.getString("httpResponseBody");
            byte[] decodedBytes = Base64.getDecoder().decode(base64Html);
            htmlBody = new String(decodedBytes, StandardCharsets.UTF_8);
        }

        if (htmlBody != null) {
            return extractTextFromHtml(htmlBody);
        }

        return "";
    }

    /**
     * Extract meaningful text from HTML, filtering out scripts, styles, and noise
     */
    private String extractTextFromHtml(String html) {
        // Remove script tags and their content
        String text = html.replaceAll("(?is)<script[^>]*>.*?</script>", " ");

        // Remove style tags and their content
        text = text.replaceAll("(?is)<style[^>]*>.*?</style>", " ");

        // Remove comments
        text = text.replaceAll("(?s)<!--.*?-->", " ");

        // Remove all HTML tags
        text = text.replaceAll("<[^>]+>", " ");

        // Decode common HTML entities
        text = text.replaceAll("&nbsp;", " ");
        text = text.replaceAll("&amp;", "&");
        text = text.replaceAll("&lt;", "<");
        text = text.replaceAll("&gt;", ">");
        text = text.replaceAll("&quot;", "\"");
        text = text.replaceAll("&#39;", "'");
        text = text.replaceAll("&apos;", "'");

        // Normalize whitespace (multiple spaces/newlines to single space)
        text = text.replaceAll("\\s+", " ");

        // Split into lines by newline for filtering
        String[] lines = text.split("\\n");
        StringBuilder cleanedText = new StringBuilder();

        for (String line : lines) {
            line = line.trim();

            // Skip very short lines (likely navigation/UI elements)
            if (line.length() < 20) {
                continue;
            }

            // Check if line has reasonable letter density
            long letterCount = line.chars().filter(Character::isLetter).count();
            long totalCount = line.length();

            // If at least 60% letters/spaces, it's probably real content
            if (letterCount > totalCount * 0.6) {
                cleanedText.append(line).append(" ");
            }
        }

        // Final cleanup: remove extra spaces
        return cleanedText.toString().trim().replaceAll("\\s+", " ");
    }
}