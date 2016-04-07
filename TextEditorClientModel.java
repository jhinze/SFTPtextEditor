/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc4380.jhinze;

import com.jcraft.jsch.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.Vector;

public class TextEditorClientModel {

    private JSch jsch;
    private Session session;
    private Channel channel;
    private ChannelSftp sftp;
    private boolean connected;

    public TextEditorClientModel() {
        jsch = new JSch();
        connected = false;
    }

    public boolean connect(String host, int port, String user, String password) {
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

    public boolean disconnect() {
        session.disconnect();
        connected = false;
        return true;
    }

    public boolean isConnected() {
        return connected;
    }

    public Vector ls(String p) {
        try {
            return sftp.ls(p);
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String pwd() {
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

    public void cd(String s) {
        if (connected) {
            try {
                sftp.cd(s);
            } catch (SftpException ex) {
                //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public String openFile(String src) {
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

    public boolean downloadFile(String src, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            InputStream in = sftp.get(src);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer);
            }
            in.close();
            fos.flush();
            fos.close();
            return true;
        } catch (SftpException | IOException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean touchFile(String s) {
        try {
            OutputStream out = sftp.put(s);
            out.close();
            return true;
        } catch (SftpException | IOException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public boolean saveFile(String s, String file) {
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

    public boolean uploadFile(File file) {
        try {
            FileInputStream fip = new FileInputStream(file);
            OutputStream out = sftp.put(file.getName());
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = fip.read(buffer)) != -1) {
                out.write(buffer);
            }
            out.flush();
            out.close();
            fip.close();
            return true;
        } catch (FileNotFoundException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SftpException | IOException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean rm(String s) {
        try {
            sftp.rm(s);
            return true;
        } catch (SftpException ex) {
            //java.util.logging.Logger.getLogger(TextEditorClientModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
