package exceptions;

public class PasswordsNotEqualException extends Exception{
    public PasswordsNotEqualException(){
        super("The Two passwords provided are not equal. Please try again");
    }
}
