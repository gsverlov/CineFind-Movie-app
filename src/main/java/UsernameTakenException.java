public class UsernameTakenException extends Exception{
    public UsernameTakenException() {
        super("Username is taken");
    }
}
