public class MovieNotFoundException extends Exception{
    public MovieNotFoundException(){
        super("No movies were found");
    }
}
