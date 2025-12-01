public class MovieAlreadyFavoritedException extends Exception{
    public MovieAlreadyFavoritedException(){
        super("The Selected Movie has already been added to favorites. If you would like to remove from favorites," +
                "you can do so in the favorites List section.");
    }
}
