package apiservices;

public class ApiConfig {
    // Add your API keys here
    public static final String NOMIC_API_KEY = System.getenv("NOMIC_API_KEY");
    public static final String PINECONE_API_KEY = System.getenv("PINECONE_API_KEY");
    public static final String PINECONE_ENVIRONMENT = "us-east-1";
    public static final String PINECONE_INDEX_NAME = "movie";
    public static final String PINECONE_HOST = "https://movie-atb75ko.svc.aped-4627-b74a.pinecone.io";
    public static final String SERPAPI_KEY = System.getenv("SERPAPI_KEY");
    public static final String ZYTE_API_KEY = System.getenv("ZYTE_API_KEY");
    public static final String OMDB_API_KEY = System.getenv("OMBD_API_KEY");
}