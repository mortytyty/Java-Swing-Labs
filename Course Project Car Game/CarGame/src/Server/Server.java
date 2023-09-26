package Server;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.TreeSet;

class Server {
    public static void main(String[] args)
    {
        ServerSocket server = null;

        try {

            server = new ServerSocket(1234);
            server.setReuseAddress(true);
            System.out.println("Server started");

            while (true) {

                Socket client = server.accept();

                System.out.println("New client connected\n" + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client);

                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        TreeSet<Integer> dataList;
        private synchronized void Serialize(){
            try(FileWriter fw = new FileWriter("src/Server/Records.txt")){
                for (Integer i : dataList){
                    fw.write(i.toString()+"\n");
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }

        }
        private void Deserialize(){
            try {
                Scanner fr = new Scanner(new FileReader("src/Server/Records.txt"));

                while (fr.hasNextLine()) {
                    String line = fr.nextLine();
                    dataList.add(Integer.parseInt(line));
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
            dataList = new TreeSet<>();

        }

        public void run()
        {
            BufferedWriter out = null;
            BufferedReader in = null;

            try {
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while (!"exit".equals(inputLine = in.readLine())) {
                    dataList.add(Integer.parseInt(inputLine));
                    Deserialize();
                    int record = dataList.size()-dataList.headSet(Integer.parseInt(inputLine)).size();

                    for(Integer number : dataList){
                        System.out.println(number);
                    }
                    System.out.println("Client score: "+inputLine);
                    System.out.println("Client place in top: "+record);
                    out.write(""+record);
                    out.newLine();
                    out.flush();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    Serialize();
                    clientSocket.close();
                    System.out.println("Client disconnected");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

