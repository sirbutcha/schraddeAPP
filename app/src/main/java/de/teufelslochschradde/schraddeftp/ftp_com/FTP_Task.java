package de.teufelslochschradde.schraddeftp.ftp_com;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by dirk on 11.04.2017.
 */

public class FTP_Task extends AsyncTask<String, Integer, Integer> {


    public static final int MSG_ID_CHDIR = 0;
    public static final int MSG_ID_UPLOAD = 1;

    private Handler mhandler;
    private InputStream mFis;


    private Butcha_FTP ftpConnection;

    public FTP_Task(Handler handler) {
        super();
        mhandler = handler;
        ftpConnection = new Butcha_FTP();
    }

    public FTP_Task() {
        super();
        ftpConnection = new Butcha_FTP();
    }

    public void SetHandler(Handler handler){
        mhandler = handler;
    }

    public Butcha_FTP getFtpConnection(){
        return ftpConnection;
    }

    public void ChangeDirectory(String folderpath){
        String[] ftpparams = {"changedir", folderpath};
        execute(ftpparams);
    }

    public void UploadFile(String filename, InputStream fis){
        String[] ftpparams = {"changedir", filename};
        mFis = fis;
    }

    @Override
    protected Integer doInBackground(String... params) {

        switch(params[0]) {
            case "changedir":
                if(!ftpConnection.CheckConnection()){
                    ftpConnection.Connect();
                }
                if(ftpConnection.ChangeFolder(params[1])){
                    return MSG_ID_CHDIR;
                }
                break;
            case "upload":
                try {
                    if(ftpConnection.storeFile(params[1],mFis)){
                        return MSG_ID_UPLOAD;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer successInd){

        switch(successInd){
            case MSG_ID_CHDIR:
                mhandler.sendEmptyMessage(MSG_ID_CHDIR);
                break;
            case MSG_ID_UPLOAD:
                mhandler.sendEmptyMessage(MSG_ID_UPLOAD);
                break;
            default:
                mhandler.sendEmptyMessage(0);
                break;
        }
    }
}
