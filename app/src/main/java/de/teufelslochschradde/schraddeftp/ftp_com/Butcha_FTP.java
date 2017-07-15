package de.teufelslochschradde.schraddeftp.ftp_com;

import android.os.Handler;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dirk on 21.03.2017.
 */

public class Butcha_FTP extends FTPClient {

    private String LOG_INFO = "FTPClient";

    public static final String HOME_DIR = "Dirk/Bilder";

    public static final int MSG_ID_CHDIR = 0;
    public static final int MSG_ID_UPLOAD = 1;

    public static final int MSG_SUCCESS = 0;
    public static final int MSG_ERROR = 1;

    private String serverAdr = "ftp.pcom.de";
    // int port = 21;
    private String user = "pcom/schradde";
    private String passw = "xrdifvfwe";

    private ArrayList<String> folders = new ArrayList<>();

    private ExecutorService mExeService;
    private Handler mHandler;

    private String currFolderPath;

    public Butcha_FTP() {
        super();
        mExeService = Executors.newSingleThreadExecutor();
    }

    public Butcha_FTP(Handler handler) {
        super();
        mExeService = Executors.newSingleThreadExecutor();
        mHandler = handler;
    }

    public void setHandler(Handler handler){
        mHandler = handler;
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
        currFolderPath = null;
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


    private boolean ChangeFolder(String folderpath) {
        boolean success =false;
        try {
            success = this.changeWorkingDirectory(folderpath);
            if (success) {
                for (String curFolder : listNames()) {
                    // Dropdown Menü Überordner füllen
//                   System.out.println(ftpClient.cwd(files[kk]));
                    if (cwd(curFolder) == 250) {
                        folders.add(curFolder);
                        Log.d(LOG_INFO, String.valueOf(cwd("../")));
                    }
                }

                currFolderPath = folderpath;
                this.setFileType(FTPClient.BINARY_FILE_TYPE);
            }
        } catch (IOException e) {
            e.printStackTrace();
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


    public void doConnect(){
        Callable<Boolean> con =  new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return Connect();
            }
        };

        Future<Boolean> future = mExeService.submit(con);
    }

    public void doChangeDir(final String path) {
        Callable<Boolean> chDir = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Boolean success = false;
                String folderpath = path;


                try {
                    if(isConnected()){
                        Log.d(LOG_INFO, printWorkingDirectory());
                        Log.d(LOG_INFO, "/schradde/" + Butcha_FTP.HOME_DIR);
                        if(printWorkingDirectory().equals("/schradde/" + Butcha_FTP.HOME_DIR)){
                            if(!folderpath.equals(".")){
                                folders.clear();
                                success = ChangeFolder(folderpath);
                            }
                        }else{
                            while(printWorkingDirectory().lastIndexOf("/") >= 20 ){
                                changeToParentDirectory();
                            }
                            if(printWorkingDirectory().equals("/schradde/" + Butcha_FTP.HOME_DIR)) {
                                folders.clear();
                                success = ChangeFolder(folderpath);
                            }
                        }
                    }else{
                        success = Connect();
                        if(success) {
                            folders.clear();
                            if (!folderpath.equals(".")) {
                                ChangeFolder(Butcha_FTP.HOME_DIR + "/" + folderpath);
                            } else {
                                ChangeFolder(Butcha_FTP.HOME_DIR);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
                Log.d(LOG_INFO, printWorkingDirectory());
                if(success){
                    mHandler.obtainMessage(MSG_ID_CHDIR, MSG_SUCCESS, 0).sendToTarget();
                }else{
                    mHandler.obtainMessage(MSG_ID_CHDIR, MSG_ERROR, 0).sendToTarget();
                }
                return success;
            }
        };

        Future<Boolean> future = mExeService.submit(chDir);
    }


    public void doUpload(final String filename, final FileInputStream fis){
        Callable<Boolean> upload = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                Boolean success = true;

                // if not connected...
                if(!CheckConnection()){
                    // try to connect
                    success = Connect();
                    // and change to last active path
                    if(success){
                        success = ChangeFolder(currFolderPath);
                    }
                }
                if(success) {
                    success = storeFile(filename, fis);
                }
                if(success){
                    mHandler.obtainMessage(MSG_ID_UPLOAD, MSG_SUCCESS, 0).sendToTarget();
                }else{
                    mHandler.obtainMessage(MSG_ID_UPLOAD, MSG_ERROR, 0).sendToTarget();
                }
                return success;
            }
        };

        Future<Boolean> future = mExeService.submit(upload);
    }

    public String getCurrFolderPath(){
        return currFolderPath;
    }

    public ArrayList<String> getFolders(){
        return folders;
    }

}
