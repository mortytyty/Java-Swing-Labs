package Client;
import Client.UI.UserInterface;
import java.io.*;


class Client {
    public static void main(String[] args)
    {
        try {
            new UserInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
