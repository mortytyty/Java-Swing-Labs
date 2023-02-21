import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TableTest extends JFrame
{
    private JTextField upLimitField;
    private JTextField stepField;
    private JTextField downLimitField;
    private DefaultTableModel tableModel;
    private JTable table1;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private Object[][] array = new String[][] {};
    private Object[] columnsHeader = new String[] {"Ниж. предел", "Вверх. предел", "Шаг","Результат"};

    public TableTest()
    {
        super("Integ App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int i, int i1) {
                if(i1==3)
                    return false;
                return true;
            }
        };
        tableModel.setColumnIdentifiers(columnsHeader);

        upLimitField = new JTextField(10);
        downLimitField = new JTextField(10);
        stepField = new JTextField(10);
        label1 = new JLabel("Нижний предел");
        label2 = new JLabel("Верхний предел");
        label3 = new JLabel("Шаг");

        table1 = new JTable(tableModel);

        JButton add = new JButton("Добавить");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = table1.getSelectedRow();
                if(upLimitField.getText().isEmpty()||downLimitField.getText().isEmpty()||stepField.getText().isEmpty())
                    JOptionPane.showMessageDialog(null,"Не все поля заполнены","Error",JOptionPane.PLAIN_MESSAGE);
                else{
                    tableModel.insertRow(idx + 1, new String[]{
                            upLimitField.getText(), downLimitField.getText(), stepField.getText()});
                }
            }
        });

        JButton remove = new JButton("Удалить");
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = table1.getSelectedRow();
                if(index==-1){
                    JOptionPane.showMessageDialog(null,"Выберите ячейку для удаления","Error",JOptionPane.PLAIN_MESSAGE);
                }else {
                    tableModel.removeRow(index);
                }
            }
        });

        JButton solve = new JButton("Рассчитать");
        solve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = table1.getSelectedRow();
                if(index==-1){
                    JOptionPane.showMessageDialog(null,"Выберите ячейку","Error",JOptionPane.PLAIN_MESSAGE);
                }else{
                    double a = 	Double.parseDouble((String) table1.getValueAt(index,0));
                    double b = 	Double.parseDouble((String)table1.getValueAt(index,1));
                    double c = 	Double.parseDouble((String)table1.getValueAt(index,2));

                    table1.setValueAt(integFunk(a,b,c),index,3);
                }
            }
        });
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel title = new JPanel();
        title.setLayout(new BoxLayout(title,BoxLayout.X_AXIS));



        JPanel first = new JPanel();
        JPanel second = new JPanel();
        JPanel third = new JPanel();
        first.setLayout(new BoxLayout(first,BoxLayout.Y_AXIS));
        second.setLayout(new BoxLayout(second,BoxLayout.Y_AXIS));
        third.setLayout(new BoxLayout(third,BoxLayout.Y_AXIS));
        first.add(label1);
        first.add(upLimitField);
        second.add(label2);
        second.add(downLimitField);
        third.add(label3);
        third.add(stepField);
        title.add(first);
        title.add(second);
        title.add(third);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(new JScrollPane(table1),BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(remove);
        buttons.add(solve);

        mainPanel.add(title,BorderLayout.NORTH);
        mainPanel.add(tablePanel,BorderLayout.CENTER);
        mainPanel.add(buttons,BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        setSize(400,300);
        setVisible(true);
    }

    private static double areaFunk(double a, double b,double h){
        return ((Math.sin(a*a)+Math.sin(b*b))/2)*h;
    }
    private static Object integFunk(double a, double b, double step){
        double res = 0 ;
       double ost= (b-a)%step;
        res+= areaFunk(a,a+ost,step);
        a+=ost;


        while(a<b){
            res+= areaFunk(a,a+step,step);
            a+=step;
        }



        return res;
    }
    public static void main(String[] args) {
        new TableTest();
    }
}
