package app.android.textviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author esprit
 */
public final class TextViewer extends Activity {
    private static final int ACTION_SHOW_PROGRESS = 0;
    private static final int ACTION_HIDE_PROGRESS = 1;
    private static final int ACTION_SET_CONTENT = 2;

    //private Uri currentUri;
    private Handler handler;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.textviewer);

        handler = new Handler() {
            private ProgressDialog dialog = new ProgressDialog(TextViewer.this);

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ACTION_SHOW_PROGRESS: {
                        dialog = new ProgressDialog(TextViewer.this);
                        dialog.setTitle(R.string.appName);
                        dialog.setMessage(getBaseContext().getString(R.string.opening));
                        dialog.show();
                    } break;
                    case ACTION_HIDE_PROGRESS: {
                        dialog.dismiss();
                    } break;
                    case ACTION_SET_CONTENT: {
                        String data[] = (String[])msg.obj;
                        updateView(data[0], data[1]);
                    } break;
                }
            }
        };

        openFile(getIntent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void openFile(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (null != uri) {
                final String path = uri.getPath();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(ACTION_SHOW_PROGRESS);
                        String content = getFileContent(path);
                        if (null != content) {
                            //currentUri = uri;
                            String data[] = new String[]{path, content};
                            handler.sendMessage(handler.obtainMessage(ACTION_SET_CONTENT, data));
                            handler.sendEmptyMessage(ACTION_HIDE_PROGRESS);
                        } else {
                            handler.sendEmptyMessage(ACTION_HIDE_PROGRESS);
                            showError(path);
                        }
                    }
                };
                thread.start();
            }
        }
    }

    private String getFileContent(String path) {
        if (null == path) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            String str;
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                while ((str = reader.readLine()) != null) {
                    sb.append(str).append('\n');
                }
                reader.close();
                return sb.toString();
            } catch (IOException e) {
            }
        }
        return null;
    }

    private void updateView(String title, String text) {
        setTitle(title);
        setText(text);
    }

    private void setText(String text) {
        TextView t = (TextView)findViewById(R.id.textView);
        t.setText(text);
    }

    private void showError(String path) {
        String template = getBaseContext().getString(R.string.cantOpen);
        String error = String.format(template, path);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}
