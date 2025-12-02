package apiservices;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NomicEmbeddingClient {
    private final String apiKey;
    private static final String EMBEDDING_URL = "https://api-atlas.nomic.ai/v1/embedding/text";
    private static final String MODEL = "nomic-embed-text-v1.5";

    public NomicEmbeddingClient(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Creates an embedding vector for the given text
     * @param text The text to embed
     * @return float array representing the embedding vector (768 dimensions)
     */
    public float[] createEmbedding(String text) throws Exception {
        URL url = new URL(EMBEDDING_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        // Build request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("texts", new JSONArray().put(text));
        requestBody.put("task_type", "search_document");

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            // Read error response body
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)
            );
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            throw new Exception("Nomic API error " + responseCode + ": " + errorResponse.toString());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Parse embedding from response
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray embeddingsArray = jsonResponse.getJSONArray("embeddings");
        JSONArray embeddingArray = embeddingsArray.getJSONArray(0);

        // Convert to float array
        float[] embedding = new float[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = (float) embeddingArray.getDouble(i);
        }

        return embedding;
    }
}