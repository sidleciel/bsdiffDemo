package com.rhodes.bsdiffdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.rhodes.bsdiffdemo.R;
import com.rhodes.bsdiffdemo.component.bsdiff.Bsdiffer;
import com.rhodes.bsdiffdemo.utils.DigestTool;
import com.rhodes.bsdiffdemo.utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by xiet on 2015/6/30.
 */
public class BsdiffDemoActivity extends Activity {

    RadioGroup functionsV;
    EditText oldV;
    EditText newV;
    EditText patchV;
    Button executeV;

    final int REQUEST_OLD = R.id.text_old;
    final int REQUEST_NEW = R.id.text_new;
    final int REQUEST_PATCH = R.id.text_patcher;
    final int FUNC_GENERATE_NEW = R.id.func_generate_new_apk;
    final int FUNC_GENERATE_PATCH = R.id.func_generate_patch;
    int functionId;

    private DiffTask generateNewTask;


    private String rootPath = Environment.getExternalStorageDirectory().getPath() + "/bsDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log("onCreate-start");
        Logger.log("onCreate-rootPath=" + rootPath);
        setContentView(R.layout.activity_diff_demo);

        initViews();
        Logger.log("onCreate-end");
    }

    private void initViews() {
        functionsV = (RadioGroup) findViewById(R.id.func_group);
        oldV = (EditText) findViewById(R.id.text_old);
        newV = (EditText) findViewById(R.id.text_new);
        patchV = (EditText) findViewById(R.id.text_patcher);
        executeV = (Button) findViewById(R.id.execute);

        functionsV.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setFunction(checkedId);
            }
        });
        functionsV.check(FUNC_GENERATE_NEW);
        Logger.log("onCreate-finctionId=FUNC_GENERATE_NEW");

        executeV.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_new:
                    Logger.log("mOnClickListener-onClick=text_new");
                    findContent(REQUEST_NEW);
                    break;
                case R.id.text_old:
                    Logger.log("mOnClickListener-onClick=text_old");
                    findContent(REQUEST_OLD);
                    break;
                case R.id.text_patcher:
                    Logger.log("mOnClickListener-onClick=text_patcher");
                    findContent(REQUEST_PATCH);
                    break;
                case R.id.execute:
                    if (!checkInputValidate()) return;
                    generateNewTask = new DiffTask();
                    Logger.log("mOnClickListener-onClick=generate_new_apk/generate_patch\n" + "generateNewTask-status=" + generateNewTask.getStatus().name());
                    generateNewTask.execute();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.log("onActivityResult-start");
        if (data == null) return;

        switch (requestCode) {
            case REQUEST_NEW:
                Logger.log("onActivityResult-requestCode=REQUEST_NEW\npath=" + data.getData().getEncodedPath());
                newV.setText(data.getData().getEncodedPath());
                break;
            case REQUEST_OLD:
                Logger.log("onActivityResult-requestCode=REQUEST_OLD\npath = " + data.getData().getEncodedPath());
                oldV.setText(data.getData().getEncodedPath());
                break;
            case REQUEST_PATCH:
                Logger.log("onActivityResult-requestCode=REQUEST_PATCH\npath=" + data.getData().getEncodedPath());
                patchV.setText(data.getData().getEncodedPath());
                break;
            default:
                break;
        }
        Logger.log("onActivityResult-end");
    }

    private void findContent(int requestCode) {
        Logger.log("findContent-start");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
        Logger.log("findContent-end");
    }

    private void setFunction(int checkedId) {
        Logger.log("setFunction-start");
        functionId = checkedId;
        if (functionId == FUNC_GENERATE_NEW) {
            Logger.log("setFunction-functionId=FUNC_GENERATE_NEW");
            oldV.setOnClickListener(mOnClickListener);
            oldV.setInputType(InputType.TYPE_NULL);
            newV.setOnClickListener(null);
            newV.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
            patchV.setOnClickListener(mOnClickListener);
            patchV.setInputType(InputType.TYPE_NULL);

            oldV.setHint(R.string.diff_old);
            newV.setHint(R.string.generate_name);
            patchV.setHint(R.string.diff_patch);
        } else if (functionId == FUNC_GENERATE_PATCH) {
            Logger.log("setFunction-functionId=FUNC_GENERATE_PATCH");
            oldV.setOnClickListener(mOnClickListener);
            oldV.setInputType(InputType.TYPE_NULL);
            newV.setOnClickListener(mOnClickListener);
            newV.setInputType(InputType.TYPE_NULL);
            patchV.setOnClickListener(null);
            patchV.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

            oldV.setHint(R.string.diff_old);
            newV.setHint(R.string.diff_new);
            patchV.setHint(R.string.generate_name);
        }
        Logger.log("setFunction-end");
    }

    private boolean checkInputValidate() {
        Logger.log("checkInputValidate-start");
        if (oldV.getText().toString().equals("")) {
            Toast.makeText(this, "请选择旧版本apk", Toast.LENGTH_SHORT).show();
            Logger.log("checkInputValidate-result=false");
            return false;
        }

        if (newV.getText().toString().equals("")) {
            Toast.makeText(this, "文件不能为空", Toast.LENGTH_SHORT).show();
            Logger.log("checkInputValidate-result=false");
            return false;
        }

        if (patchV.getText().toString().equals("")) {
            Toast.makeText(this, "文件不能为空", Toast.LENGTH_SHORT).show();
            Logger.log("checkInputValidate-result=false");
            return false;
        }

        Logger.log("checkInputValidate-result=true");
        return true;
    }

    private void installApk(String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        startActivity(i);
    }

    private void processTaskPost(final String generateFilePath) {
        Logger.log("processTaskPost-start");
        File newFile = new File(generateFilePath);
        if (newFile.exists() && newFile.isFile()) {
            try {
                String sha1sum = DigestTool.getFileSha1(newFile.getAbsolutePath());
                AlertDialog.Builder builder = new AlertDialog.Builder(BsdiffDemoActivity.this);
                builder.setMessage(sha1sum);
                builder.setTitle("新包校验");
                if (functionId == FUNC_GENERATE_NEW)
                    builder.setPositiveButton("安装", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            installApk(generateFilePath);
                        }
                    });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.log("processTaskPost-end");
    }

    class DiffTask extends AsyncTask {

        private ProgressDialog progressDialog;
        String generateFilePath;

        @Override
        protected void onPostExecute(Object result) {
            Logger.log("generateNewTask-onPostExecute");
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            Toast.makeText(BsdiffDemoActivity.this, "打包完成......", Toast.LENGTH_SHORT).show();
            processTaskPost(generateFilePath);
        }

        @Override
        protected void onPreExecute() {
            Logger.log("generateNewTask-onPreExecute");
            super.onPreExecute();
            progressDialog = ProgressDialog.show(BsdiffDemoActivity.this, "正在生成...", "请稍等...", true, false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            Logger.log("generateNewTask-doInBackground");

            if (functionId == FUNC_GENERATE_NEW)
                generateFilePath = rootPath + File.separator + newV.getText().toString() + ".apk";
            else {
                generateFilePath = rootPath + File.separator + patchV.getText().toString();
            }
            File file = new File(generateFilePath);
            if (file.exists())
                file.delete();
            if (!file.getParentFile().exists()) {
                Logger.log("generateNewTask-file.getParentFile()=" + file.getParentFile().getAbsolutePath());
                file.getParentFile().mkdirs();
            }

            Logger.log("generateNewTask-doInBackground--processing...");
            if (functionId == FUNC_GENERATE_NEW) {
                Logger.log("generateNewTask-doInBackground--processing \nold=" + oldV.getText().toString() + "\nnew=" + generateFilePath + "\npatch=" + patchV.getText().toString());
                Bsdiffer.patch(oldV.getText().toString(), generateFilePath, patchV.getText().toString());
            } else {
                Logger.log("generateNewTask-doInBackground--processing \nold=" + oldV.getText().toString() + "\nnew=" + newV.getText().toString() + "\npatch=" + generateFilePath);
                Bsdiffer.diff(oldV.getText().toString(), newV.getText().toString(), generateFilePath);
            }
            Logger.log("generateNewTask-doInBackground--processing stop...");
            return null;
        }
    }
}
