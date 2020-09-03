import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends javax.mail.Authenticator
{
    private final String login   ;
    private final String password;
    public EmailAuthenticator (final String login, final String password)
    {
        this.login    = "your mail";
        this.password = "pass";
    }
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(login, password);
    }
}