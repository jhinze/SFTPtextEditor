/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc4380.jhinze;

import com.jcraft.jsch.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JLabel;

public class TextEditorClientModel {

    private final JSch jsch;
    private Session session;
    private Channel channel;
    private ChannelSftp sftp;
    private boolean connected;

    public TextEditorClientModel() {
        jsch = new JSch();
        connected = false;
    }

    public JSch getJSch() {
        return jsch;
    }

    public synchronized boolean connect(String host, int port, String user, String password) {
        try {
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            connected = true;
            return true;
        } catch (JSchException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public synchronized boolean disconnect() {

        session.disconnect();
        connected = false;
        return true;

    }

    public synchronized boolean isConnected() {

        return connected;

    }

    public synchronized Vector ls(String p) {

        try {
            return sftp.ls(p);
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public synchronized String pwd() {

        if (connected) {
            try {
                return sftp.pwd();
            } catch (SftpException ex) {
                //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
                return "-1";
            }
        } else {
            return "-1";
        }

    }

    public synchronized boolean cd(String s) {

        try {
            sftp.cd(s);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized String openFile(String src) {

        String s = null;
        try {
            Scanner sc = new Scanner(sftp.get(src), "UTF-8").useDelimiter("\\A");
            s = sc.hasNext() ? sc.next() : "";
            sc.close();
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;

    }

    public synchronized boolean downloadFile(String src, File file, final JLabel label) {

        try {
            sftp.get(src, file.getAbsoluteFile().toString(), new SftpProgressMonitor() {
                long max;
                long total;

                @Override
                public void init(int i, String string, String string1, long l) {
                    label.setText("Downloading..");
                    max = l;
                }

                @Override
                public boolean count(long l) {
                    total += l;
                    int d = (int) (total * 100.0 / max + .5);
                    label.setText("Downloading " + d + "%");
                    return true;
                }

                @Override
                public void end() {
                    label.setText("Connected");
                }

            });
            return true;
        } catch (SftpException ex) {
            java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean touchFile(String s) {

        try {
            OutputStream out = sftp.put(s);
            out.close();
            return true;
        } catch (SftpException | IOException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean mkdir(String s) {

        try {
            sftp.mkdir(s);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean saveFile(String s, String file) {

        try {
            DataOutputStream dos = new DataOutputStream(sftp.put(file));
            dos.writeBytes(s);
            dos.flush();
            dos.close();
            return true;
        } catch (SftpException | IOException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    public synchronized boolean uploadFile(File file, String dest, final JLabel label) {

        try {
            sftp.put(file.getAbsoluteFile().toString(), dest, new SftpProgressMonitor() {
                long max;
                long total;

                @Override
                public void init(int i, String string, String string1, long l) {
                    label.setText("Uploading..");
                    max = l;
                }

                @Override
                public boolean count(long l) {
                    total += l;
                    int d = (int) (total * 100.0 / max + .5);
                    label.setText("Uploading " + d + "%");
                    return true;
                }

                @Override
                public void end() {
                    label.setText("Connected");
                }

            });
            return true;
        } catch (SftpException e) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean rename(String s, String name) {

        try {
            sftp.rename(s, name);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean rmdir(String s) {

        try {
            sftp.rmdir(s);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public synchronized boolean rm(String s) {
        try {
            sftp.rm(s);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
