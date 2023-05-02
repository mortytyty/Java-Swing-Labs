package Server;

import java.io.*;
import java.net.*;

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

        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
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
                    String[] splitLine = inputLine.split(" ");
                    Solver solver = new Solver(Double.parseDouble(splitLine[0]), Double.parseDouble(splitLine[1]), Double.parseDouble(splitLine[2]));
                    out.write(solver.solve());
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
