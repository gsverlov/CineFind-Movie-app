import io.pinecone.clients.Pinecone;
import io.pinecone.clients.Index;
import java.util.ArrayList;
import java.util.List;
public class TestApiSetup {

    public static void main(String[] args) {


        // Test 1: Nomic Embedding
        System.out.println("1. Testing Nomic Embedding API...");
        try {
            NomicEmbeddingClient embeddingClient = new NomicEmbeddingClient(ApiConfig.NOMIC_API_KEY);
            float[] embedding = embeddingClient.createEmbedding("test movie about space");
            System.out.println("success");
        } catch (Exception e) {
            System.out.println("fail");
        }

        System.out.println();

        // Test 2: Pinecone Connection
        System.out.println("2. Testing Pinecone Connection...");
        try {
            // Initialize Pinecone client
            Pinecone pc = new Pinecone.Builder(ApiConfig.PINECONE_API_KEY).build();

            System.out.println("  connecting to index '" + ApiConfig.PINECONE_INDEX_NAME + "'...");

            // Connect directly using index name
            Index index = pc.getIndexConnection(ApiConfig.PINECONE_INDEX_NAME);

            // Create a test vector matching your index dimensions (768)
            List<Float> testVector = new ArrayList<>();
            for (int i = 0; i < 768; i++) {
                testVector.add((float) Math.random());
            }

            // Create metadata using Struct
            com.google.protobuf.Struct metadata = com.google.protobuf.Struct.newBuilder()
                    .putFields("title", com.google.protobuf.Value.newBuilder().setStringValue("Test Movie").build())
                    .putFields("test", com.google.protobuf.Value.newBuilder().setBoolValue(true).build())
                    .build();

            // Upsert the vector
            index.upsert("test_vector", testVector, null, null, metadata, null);

            System.out.println("   success");
        } catch (Exception e) {
            System.out.println("   fail " + e.getMessage());
        }
        // Test 3: SerpAPI
        System.out.println("3. Testing SerpAPI...");
        try {
            SerpApiClient serpClient = new SerpApiClient(ApiConfig.SERPAPI_KEY);
            String url = serpClient.searchFirstUrl("The Matrix movie");
            if (url != null && !url.isEmpty()) {
                System.out.println("success");
            } else {
                System.out.println("fail");
            }
        } catch (Exception e) {
            System.out.println("fail");
        }

        System.out.println();

        // Test 4: Zyte API
        System.out.println("4. Testing Zyte API...");
        try {
            ZyteApiClient zyteClient = new ZyteApiClient(ApiConfig.ZYTE_API_KEY);
            String content = zyteClient.scrapeUrl("https://www.imdb.com/title/tt1877830/plotsummary/");
            if (content != null && content.length() > 100) {
                System.out.println(" success");
                System.out.println("   Preview: " + content.substring(0, Math.min(10000, content.length())) + "...");
            } else {
                System.out.println("too short");
            }
        } catch (Exception e) {
            System.out.println("fail");
        }

        System.out.println();;

    }
}