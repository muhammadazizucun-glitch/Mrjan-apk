package uz.mrjan.app;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView w;
    private SharedPreferences sp;
    private static final String KEY = "server_url";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        sp = getSharedPreferences("mrjan", MODE_PRIVATE);

        w = new WebView(this);
        setContentView(w);

        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setDomStorageEnabled(true);
        w.getSettings().setDatabaseEnabled(true);
        w.getSettings().setAllowFileAccess(true);

        w.setBackgroundColor(Color.parseColor("#070b14"));

        w.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, int code, String desc, String url) {
                showSetup(true);
            }
        });

        String url = sp.getString(KEY, "");
        if (url.isEmpty()) {
            showSetup(false);
        } else {
            w.loadUrl(url);
        }
    }

    private void showSetup(boolean err) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("MRJAN");

        final EditText input = new EditText(this);
        input.setHint("http:// yoki https://...");
        if (err) {
            input.setText(sp.getString(KEY, ""));
        }

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(50, 30, 50, 10);
        if (err) {
            TextView t = new TextView(this);
            t.setText("Ulanishda xatolik! Server manzilini tekshiring:");
            t.setTextColor(Color.RED);
            l.addView(t);
        }
        l.addView(input);
        b.setView(l);

        b.setPositiveButton("Saqlash", (d, w) -> {
            String u = input.getText().toString().trim();
            if (!u.startsWith("http://") && !u.startsWith("https://")) {
                u = "http://" + u;
            }
            sp.edit().putString(KEY, u).apply();
            MainActivity.this.w.loadUrl(u);
        });

        b.setNegativeButton("Chiqish", (d, w) -> finish());

        b.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && w.canGoBack()) {
            w.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        } else if (item.getItemId() == 2) {
            w.reload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

