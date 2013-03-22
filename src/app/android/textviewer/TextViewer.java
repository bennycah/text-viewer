package app.android.textviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.textviewer);

        openFile(getIntent());
    }

    private void openFile(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            String path = intent.getData().getPath();
            new OpenFileTask().execute(path);
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
        TextView t = (TextView)findViewById(R.id.textView);
        t.setText(text);
    }

    private void showError(String path) {
        String error = String.format(getString(R.string.cantOpen), path);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private class OpenFileTask extends AsyncTask<String, Void, String[]> {
        private ProgressDialog dialog = new ProgressDialog(TextViewer.this);

        @Override
        protected String[] doInBackground(String ... param) {
            String path = param[0];
            String content = getFileContent(path);
            return new String[]{path, content};
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle(R.string.appName);
            dialog.setMessage(getString(R.string.opening));
            dialog.show();
        }

        @Override
        protected void onPostExecute(String[] param) {
            String path = param[0];
            String content = param[1];
            if (null != content) {
                updateView(path, content);
            } else {
                showError(path);
            }
            dialog.dismiss();
        }
    }
}
