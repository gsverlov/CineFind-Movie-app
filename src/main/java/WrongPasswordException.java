public class WrongPasswordException extends Exception{
    public WrongPasswordException(){
        super("Invalid Password");
    }
}
