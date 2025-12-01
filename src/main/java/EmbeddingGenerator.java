import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.*;

public class EmbeddingGenerator {

    private final String apiKey;

    public EmbeddingGenerator(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Embed text for search queries (student preferences)
     * Use this when embedding user input for searching
     */
    public float[] embedQuery(String text) throws IOException {
        return embedText(text, "search_query");
    }

    /**
     * Embed text for documents (movie content)
     * Use this when storing movies in the database
     */
    public float[] embedDocument(String text) throws IOException {
        return embedText(text, "search_document");
    }

    /**
     * Generic embedding method
     */
    private float[] embedText(String text, String taskType) throws IOException {
        URL url = new URL("https://api-atlas.nomic.ai/v1/embedding/text");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");

        // Build JSON body
        JSONObject body = new JSONObject();
        body.put("model", "nomic-embed-text-v1.5");
        body.put("task_type", taskType);
        body.put("texts", new JSONArray().put(text));

        byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
        conn.setFixedLengthStreamingMode(bodyBytes.length);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(bodyBytes);
        }

        int status = conn.getResponseCode();

        // Read response
        InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder resp = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resp.append(line);
            }
        }

        conn.disconnect();

        // Handle errors
        if (status < 200 || status >= 300) {
            throw new IOException("Nomic API error (" + status + "): " + resp.toString());
        }

        // Parse embedding
        JSONObject json = new JSONObject(resp.toString());
        JSONArray embeddings = json.getJSONArray("embeddings");
        JSONArray arr = embeddings.getJSONArray(0);

        float[] vector = new float[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            vector[i] = (float) arr.getDouble(i);
        }

        return vector;
    }
}