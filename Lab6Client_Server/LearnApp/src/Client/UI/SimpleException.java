package Client.UI;

import javax.swing.*;

public class SimpleException extends Exception {
    public SimpleException(String msg){
        JOptionPane.showMessageDialog(null,msg,"Exception",JOptionPane.PLAIN_MESSAGE);
    }
}
