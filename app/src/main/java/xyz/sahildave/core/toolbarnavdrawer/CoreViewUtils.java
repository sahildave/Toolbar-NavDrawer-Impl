package xyz.sahildave.core.toolbarnavdrawer;

import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sahil on 21/9/15.
 */
public class CoreViewUtils {
    private static final String LOG_TAG = CoreViewUtils.class.getName();

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static SpannableStringBuilder getBoldedText(SpannableStringBuilder fullText, String textToBold) {
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        Pattern p = Pattern.compile(textToBold, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(fullText);
        while (m.find()) {
            fullText.setSpan(bss, m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return fullText;
    }
}
