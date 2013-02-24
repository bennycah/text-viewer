package app.android.textviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
public class TextViewer extends Activity {
    //private Uri currentUri;
    
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.textviewer);

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
                String path = uri.getPath();
                String content = getFileContent(path);
                if (null != content) {
                    //currentUri = uri;
                    updateView(path, content);
                } else {
                    showError(path);
                }
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
