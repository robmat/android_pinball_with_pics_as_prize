package com.dozingcatsoftware.bouncy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import com.batodev.pinball.R;

public class AboutActivity extends Activity {
    // Rules text per table, indexed by level - 1. Referenced directly rather than looked up by
    // name through reflection on R.string: R8 strips R's fields in release builds, so the
    // reflective lookup silently failed and the rules never showed.
    private static final int[] TABLE_RULES = {
        R.string.table1_rules,
        R.string.table2_rules,
        R.string.table3_rules,
        R.string.table4_rules,
        R.string.table5_rules,
        R.string.table6_rules,
        R.string.table7_rules,
    };

    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);

        this.getWindow().setNavigationBarColor(Color.BLACK);

        // Get text to display by replacing "[TABLE_RULES]" with the contents of string resource
        // with ID table[level]_rules.
        String baseText = getString(R.string.about_text);
        int level = getIntent().getIntExtra("level", 1);
        String tableRulesText =
                (level >= 1 && level <= TABLE_RULES.length) ? getString(TABLE_RULES[level - 1]) : "";
        String displayText = baseText.replace("[TABLE_RULES]", tableRulesText);

        TextView tv = findViewById(R.id.aboutTextView);
        tv.setText(displayText);
        // Padding based on screen
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int padding = Math.min(metrics.widthPixels, metrics.heightPixels) / 25;
        tv.setPadding(padding, padding, padding, padding);

        // Use larger text on physically larger screens.
        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        float minInches = Math.min(widthInches, heightInches);
        float fontSize = minInches > 4 ? 18f : (minInches > 3.5 ? 16f : 14f);
        tv.setTextSize(fontSize);
        // Uncomment to see the pixel dimensions and computed physical size.
        // tv.setText(metrics.widthPixels + ":" + metrics.heightPixels + ":" + widthInches + ":" + heightInches + "\n" + tv.getText());
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Return to main activity when "P" is pressed, to avoid accidentally quitting by hitting
        // "back" multiple times. See https://github.com/dozingcat/Vector-Pinball/issues/103.
        if (keyCode == KeyEvent.KEYCODE_P) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Intent startForLevel(Context context, int level) {
        Intent aboutIntent = new Intent(context, AboutActivity.class);
        aboutIntent.putExtra("level", level);
        context.startActivity(aboutIntent);
        return aboutIntent;
    }
}
