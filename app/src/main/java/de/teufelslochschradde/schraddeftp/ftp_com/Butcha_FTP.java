package de.teufelslochschradde.schraddeftp.ftp_com;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by dirk on 21.03.2017.
 */

public class Butcha_FTP extends FTPClient {

    private String serverAdr = "ftp.pcom.de";
    // int port = 21;
    private String user = "pcom/schradde";
    private String passw = "xrdifvfwe";

    private ArrayList<String> folders = new ArrayList<>();
    private String LOG_INFO = "FTPClient";


    public Butcha_FTP() {
        super();
    }

    public boolean Connect(String serverAdr, String user, String passw){
        boolean success;
        try {
            this.connect(serverAdr);
            showServerReply();
            this.enterLocalPassiveMode();
            int replyCode = this.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                Log.d(this.LOG_INFO, "Connect failed");
                return false;
            }
            success = this.login(user, passw);
            showServerReply();
            if (!success) {
                Log.d(this.LOG_INFO, "Connection could not be established");
                return false;
            }
        }
        catch (IOException ex) {
            Log.d(this.LOG_INFO, "Exception occurred!");
            return false;
        }
        return success;
    }

    public boolean Connect(){
        boolean success;
        try {
            this.connect(this.serverAdr);
            showServerReply();
            this.enterLocalPassiveMode();
            int replyCode = this.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                Log.d(LOG_INFO, "Connect failed");
                return false;
            }
            success = this.login(this.user, this.passw);
            showServerReply();
            if (!success) {
                Log.d(LOG_INFO, "Connection could not be established");
                return false;
            }
        }
        catch (IOException ex) {
            Log.d(LOG_INFO, "Exception occurred!");
            return false;
        }
        return success;
    }


    public boolean CheckConnection(){
        boolean answer = false;
        try {
            answer = this.sendNoOp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return answer;
    }


    public boolean ChangeFolder(String folderpath) {
        boolean success;
        try {
            success = this.changeWorkingDirectory(folderpath);
            if(success){
                for(int i = 0; i < this.listNames().length; i++)
                folders.add(this.listNames()[i]);
            }

            this.setFileType(FTPClient.BINARY_FILE_TYPE);
        }
        catch (IOException ex) {
            Log.d(LOG_INFO, "Exception occurred! in ChangeFolder");
            return false;
        }
        return success;
    }


    private boolean CheckIfFileExists(String filepath){
        InputStream inputStream = null;
        int returnCode = 0;
        try {
            inputStream = this.retrieveFileStream(filepath);
            showServerReply();
            this.printWorkingDirectory();
            returnCode = this.getReplyCode();
        } catch (IOException ex) {
            Log.d(LOG_INFO, "File Exists Check failed" + ex.toString());
        }
        return !(inputStream == null || returnCode == 550);
    }


    private void showServerReply() {
        String[] replies = this.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                Log.d(LOG_INFO, "SERVER: " + aReply);
            }
        }
    }


    public ArrayList<String> getFolders(){
        return folders;
    }

}
