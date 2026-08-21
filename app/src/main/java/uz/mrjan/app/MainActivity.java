package uz.mrjan.app;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    private SharedPreferences sp;
    private static final String KEY = "server_url";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        sp = getSharedPreferences("mrjan", MODE_PRIVATE);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        web.setBackgroundColor(Color.parseColor("#070b14"));

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, int code, String desc, String url) {
                showSetup(true);
            }
        });

        String url = sp.getString(KEY, "");
        if (url.isEmpty()) {
            showSetup(false);
        } else {
            web.loadUrl(url);
        }
    }

    private void showSetup(boolean error) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(50, 40, 50, 40);

        TextView t = new TextView(this);
        if (error) {
            t.setText("Serverga ulanmadi. Manzilni tekshiring.");
        } else {
            t.setText("Korxona server manzilini kiriting");
        }
        box.addView(t);

        final EditText in = new EditText(this);
        in.setText(sp.getString(KEY, "http://192.168.1.100:5000"));
        box.addView(in);

        new AlertDialog.Builder(this)
                .setTitle("MRJAN")
                .setView(box)
                .setCancelable(false)
                .setPositiveButton("Saqlash", (d, w) -> {
                    String u = in.getText().toString().trim();
                    if (!u.startsWith("http")) {
                        u = "http://" + u;
                    }
                    sp.edit().putString(KEY, u).apply();
                    web.loadUrl(u);
                })
                .setNegativeButton("Chiqish", (d, w) -> finish())
                .show();
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent e) {
        if (code == KeyEvent.KEYCODE_BACK && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(code, e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        m.add(0, 1, 0, "Server manzili");
        m.add(0, 2, 0, "Yangilash");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            showSetup(false);
            return true;
        }
        if (item.getItemId() == 2) {
            web.reload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
