package erasmusserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public class Server {

    private ServerSocket serverSocket;
    private static int port = 2000;
    private static ArrayList<Socket> sockets;
    private static ArrayList<PrintWriter> outputs;
    private static ArrayList<String> users = new ArrayList<String>();
    private int numberOfUsers = 0;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        init();
    }

    private void init() {
        try {

            sockets = new ArrayList<Socket>();
            serverSocket = new ServerSocket(port);
            outputs = new ArrayList<PrintWriter>();
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            System.out.println("waiting .....");
            while (true) {

                Socket s = serverSocket.accept();

                System.out.println((++numberOfUsers) + " users ");
                sockets.add(s);
                receiver(s);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void receiver(Socket s) {
        new Thread() {
            public void run() {
                String name = null;
                PrintWriter output = null;
                try {
                    output = new PrintWriter(s.getOutputStream(), true);
                    try {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        outputs.add(output);
                        BufferedReader read = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        System.out.println(s.getLocalAddress().toString() + "  , is Connected =" + s.isConnected());
                        while (s.isConnected()) {
                            output = new PrintWriter(s.getOutputStream(), true);
                            String str = read.readLine();
                            if (str == null) {
                                break;
                            }
                            System.out.println("message =" + str);
                            if (str.startsWith("Name:")) {
                                name = str.replace("Name:", "");
                                if (!users.contains(name)) {
                                    output.println("Name:" + name);
                                    send(name+" just Sigh in");
                                    users.add(name);
                                } else {
                                    output.println("Name:null");
                                }
                            } else if (str.startsWith("Reseive_Users_Request:")) {
                                String allUsersString = "AllUsers:";

                                for (int i = 0; i < users.size(); i++) {
                                    allUsersString += users.get(i);
                                    if (i != users.size() - 1) {
                                        allUsersString += "@@";
                                    }
                                }
                                send(allUsersString);
                                System.out.println(allUsersString);

                            } else if (str.startsWith("PrivateMessageFrom:")) {
                                String string2 = str.substring(19, str.length());
                                String[] fields = string2.split("@@To@@");

                                if (fields[0].equals(name) || fields[1].equals(name)) {
                                    
                                    send("PrivateMessageFrom:" + fields[0] + "@@To@@" + fields[1] + "@@To@@" + fields[2]);
                                }
                            } else {
                                send(str);
                            }
                        }
                    } catch (Exception ex) {

                    } finally {
                        outputs.remove(output);
                        users.remove(name);
                        sockets.remove(s);
                        System.out.println(outputs.size());
                        System.out.println(sockets.size());
                        String allUsersString = "AllUsers:";

                        for (int i = 0; i < users.size(); i++) {
                            allUsersString += users.get(i);
                            if (i != users.size() - 1) {
                                allUsersString += "@@";
                            }
                        }
                        send(allUsersString);
                        System.out.println(allUsersString);
                    }
                    System.out.print("One User Left, ");
                    System.out.println((--numberOfUsers) + " users remains ");
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    output.close();
                }
            }
        }.start();

    }

    private void send(String s) {
        new Thread() {
            public void run() {

                for (PrintWriter writer : outputs) {
                    //   if (out != writer) {            }
                    writer.println(s);
                }
            }
        }.start();

    }
    
//    private void sendPrivate(String from, String to, String msg) {
//        new Thread() {
//            public void run() {
//             
//                for (MyWriter writer : outputs) {
//                     //  System.out.println("PrivateMessageFrom:" + from + "@@To@@" + to + "@@To@@" + msg+"    writer.getUserName()= "+writer.getUserName());
//                    //   if (out != writer) {  
//                if( writer!=null&&writer.getUserName()!=null&&writer.getUserName().equals(to)||writer.getUserName().equals(from))
//                    writer.println("PrivateMessageFrom:" + from + "@@To@@" + to + "@@To@@" + msg);
//                }
//            }
//        }.start();
//
//    }

//    private class MyWriter extends PrintWriter {
//
//        private String userName;
//
//        public MyWriter(Writer out) {
//            super(out);
//        }
//
//        public MyWriter(Writer out, boolean b) {
//            super(out, b);
//        }
//
//        public MyWriter(OutputStream out, boolean autoFlush) {
//            super(out, autoFlush);
//        }
//
//        public String getUserName() {
//            return userName;
//        }
//
//        public void setUserName(String userName) {
//            this.userName = userName;
//        }
//
//    }
}
