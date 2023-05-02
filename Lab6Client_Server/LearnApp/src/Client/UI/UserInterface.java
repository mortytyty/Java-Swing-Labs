package Client.UI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;


public class UserInterface extends JFrame
{
    private final JTextField upLimitField;
    private final JTextField stepField;
    private final JTextField downLimitField;
    private final DefaultTableModel tableModel;
    private final JTable table1;
    LinkedList<RecordIntegral> dataList;
    Socket socket;
    BufferedWriter out;
    BufferedReader in;

    public UserInterface() throws IOException {
        super("Swing App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        socket = new Socket("localhost", 1234);

        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    out.write("exit");
                    out.newLine();
                    out.flush();

                    in.close();
                    out.close();
                    socket.close();
                }
                catch (IOException a){
                    a.printStackTrace();
                }

                super.windowClosing(e);
            }
        });


        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return i1 != 3;
            }
        };
        Object[] columnsHeader = new String[]{"Ниж. предел", "Верх. предел", "Шаг", "Результат"};
        tableModel.setColumnIdentifiers(columnsHeader);
        upLimitField = new JTextField(10);
        downLimitField = new JTextField(10);
        stepField = new JTextField(10);
        JLabel label1 = new JLabel("Нижний предел:");
        JLabel label2 = new JLabel("Верхний предел:");
        JLabel label3 = new JLabel("Шаг:");
        table1 = new JTable(tableModel);
        dataList = new LinkedList<>();

        JButton add = new JButton("Добавить");
        add.addActionListener(e -> {
            try {
                double a = Double.parseDouble(downLimitField.getText());
                double b = Double.parseDouble(upLimitField.getText());
                double c = Double.parseDouble(stepField.getText());
                if ((b - a) < c) throw new SimpleException("Шаг превышает длинну отрезка интегрирования");
                dataList.add(new RecordIntegral(a, b, c));
                tableModel.addRow(new Object[]{a, b, c});
            } catch (SimpleException ignored) {
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Не все поля заполнены", "Exception", JOptionPane.PLAIN_MESSAGE);
            }
        });

        JButton remove = new JButton("Удалить");
        remove.addActionListener(e -> {
            try {
                int index = table1.getSelectedRow();
                if (index == -1) {
                    throw new SimpleException("Не выбрана строка");
                }
                int rowCount = tableModel.getRowCount();
                int curIndex = dataList.size() - rowCount + index;
                tableModel.removeRow(index);
                dataList.remove(curIndex);
            } catch (SimpleException ignored) {
            }
        });

        JButton removeAll = new JButton("Удалить все");
        removeAll.addActionListener(e -> {
            try {
                if (table1.getRowCount() == 0) {
                    throw new SimpleException("Таблица пуста");
                }
                while (tableModel.getRowCount() != 0) {
                    tableModel.removeRow(0);
                }
                dataList.clear();
            } catch (SimpleException ignored) {
            }
        });

        JButton solve = new JButton("Рассчитать");
        solve.addActionListener(e -> {
            try {
                int index = table1.getSelectedRow();
                if (index == -1) {
                    throw new SimpleException("Не выбрана строка");
                }
                int rowCount = tableModel.getRowCount();
                int curIndex = dataList.size() - rowCount + index;

                out.write(dataList.get(curIndex).toString());
                out.newLine();
                out.flush();

                dataList.get(curIndex).setResult(in.readLine());
                tableModel.setValueAt(dataList.get(curIndex).getResult(), index, 3);
            } catch (IOException a) {
                a.printStackTrace();
            }
            catch (SimpleException ignored){}
        });

        JButton clear = new JButton("Очистить");
        clear.addActionListener(e -> {
            while (tableModel.getRowCount() != 0) {
                tableModel.removeRow(0);
            }
        });

        JButton fill = new JButton("Заполнить");
        fill.addActionListener(e -> {
            while (tableModel.getRowCount() != 0) {
                tableModel.removeRow(0);
            }
            for (RecordIntegral i : dataList) {
                tableModel.addRow(new Object[]{i.getDownLimit(), i.getUpLimit(), i.getStep(), i.getResult()});
            }
        });

        JButton serializeBit = new JButton("Записать в bin");
        serializeBit.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./src"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith("bin");
                }

                @Override
                public String getDescription() {
                    return "Только используемые";
                }
            });

            if (fileChooser.showDialog(getContentPane(), "Выбрать") == 0) {
                String fileName = fileChooser.getSelectedFile().getName();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/" + fileName))) {
                    if (!fileName.endsWith("bin")) throw new SimpleException("Выбран неверный тип файла");
                    oos.writeObject(dataList);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка сериализации", "Exception", JOptionPane.PLAIN_MESSAGE);
                } catch (SimpleException ignored) {
                }
            }
        });

        JButton serializeTxt = new JButton("Записать в txt");
        serializeTxt.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./src"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith("txt");
                }

                @Override
                public String getDescription() {
                    return "Только используемые";
                }
            });


            if (fileChooser.showDialog(getContentPane(), "Выбрать") == 0) {
                String fileName = fileChooser.getSelectedFile().getName();
                try (FileWriter fw = new FileWriter("src/" + fileName)) {
                    if (!fileName.endsWith("txt")) throw new SimpleException("Выбран неверный тип файла");
                    for (RecordIntegral i : dataList) {
                        fw.write(i.toString());
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка сериализации", "Exception", JOptionPane.PLAIN_MESSAGE);
                } catch (SimpleException ignored) {
                }
            }
        });


        JButton deserializeBit = new JButton("Считать из bin файла");
        deserializeBit.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./src"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith("bin");
                }

                @Override
                public String getDescription() {
                    return "Только используемые";
                }
            });

            if (fileChooser.showDialog(getContentPane(), "Выбрать") == 0) {
                String fileName = fileChooser.getSelectedFile().getName();
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/" + fileName))) {
                    if (!fileName.endsWith("bin")) throw new SimpleException("Выбран неверный тип файла");
                    while (tableModel.getRowCount() != 0) {
                        tableModel.removeRow(0);
                    }
                    dataList.clear();
                    dataList = (LinkedList<RecordIntegral>) ois.readObject();

                    for (RecordIntegral i : dataList) {
                        tableModel.addRow(new Object[]{i.getDownLimit(), i.getUpLimit(), i.getStep(), i.getResult()});
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка десериализации", "Exception", JOptionPane.PLAIN_MESSAGE);
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "В программе отсутствует соответствующий класс", "Exception", JOptionPane.PLAIN_MESSAGE);
                } catch (SimpleException ignored) {
                }

            }

        });

        JButton deserializeTxt = new JButton("Считать из txt файла");
        deserializeTxt.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("./src"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith("txt");
                }

                @Override
                public String getDescription() {
                    return "Только используемые";
                }
            });

            if (fileChooser.showDialog(getContentPane(), "Выбрать") == 0) {
                String fileName = fileChooser.getSelectedFile().getName();
                try (Scanner fr = new Scanner(new FileReader("src/" + fileName))) {
                    if (!fileName.endsWith("txt")) throw new SimpleException("Выбран неверный тип файла");
                    while (tableModel.getRowCount() != 0) {
                        tableModel.removeRow(0);
                    }
                    dataList.clear();

                    while (fr.hasNextLine()) {
                        String line = fr.nextLine();
                        String[] values = line.split(" ");
                        dataList.add(new RecordIntegral(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])));
                        if (values.length > 3) dataList.getLast().setResult(values[3]);
                    }
                    for (RecordIntegral i : dataList) {
                        tableModel.addRow(new Object[]{i.getDownLimit(), i.getUpLimit(), i.getStep(), i.getResult()});
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка десериализации", "Exception", JOptionPane.PLAIN_MESSAGE);
                } catch (SimpleException ignored) {
                }

            }

        });

//        Расположение элементов

        JPanel mainPanel = new JPanel();
        JPanel upPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel downPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout());
        upPanel.setLayout(new BoxLayout(upPanel, BoxLayout.Y_AXIS));

        JPanel upButtons = new JPanel();
        JPanel fields = new JPanel();

        upButtons.setLayout(new GridLayout(1, 6, 10, 0));
        upButtons.add(add);
        upButtons.add(remove);
        upButtons.add(removeAll);
        upButtons.add(solve);
        upButtons.add(fill);
        upButtons.add(clear);
        upButtons.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));


        upPanel.add(fields);
        upPanel.add(upButtons);
        upPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fields.setLayout(new GridLayout(2, 3, 10, 0));
        fields.add(label1);
        fields.add(label2);
        fields.add(label3);
        fields.add(downLimitField);
        fields.add(upLimitField);
        fields.add(stepField);

        centerPanel.add(new JScrollPane(table1), BorderLayout.CENTER);

        downPanel.add(serializeBit);
        downPanel.add(serializeTxt);
        downPanel.add(deserializeBit);
        downPanel.add(deserializeTxt);
        downPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

//        Изменение шрифта
        {
            label1.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            label2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            label3.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            solve.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            add.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            remove.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            removeAll.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            clear.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            fill.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            serializeBit.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            serializeTxt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            deserializeBit.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            deserializeTxt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            upLimitField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            downLimitField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            stepField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        }

        mainPanel.add(upPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(downPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}





