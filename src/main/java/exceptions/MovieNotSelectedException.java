package exceptions;

public class MovieNotSelectedException extends Exception {
    public MovieNotSelectedException(){
        super("Please Select a movie");
    }
}
