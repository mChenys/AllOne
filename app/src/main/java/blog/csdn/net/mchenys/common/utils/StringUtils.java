package blog.csdn.net.mchenys.common.utils;


import android.content.Context;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.pc.framwork.utils.app.ToastUtils;


public class StringUtils {

    public static boolean isBlank(String str) {
        return isEmpty(str);
    }

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())
                && !"{}".equalsIgnoreCase(value.trim())
                && !"[]".equalsIgnoreCase(value.trim())
                && !"0".equalsIgnoreCase(value.trim())) {
            return false;
        }
        return true;
    }

    public static boolean isEmptyIgnoreZero(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())
                && !"{}".equalsIgnoreCase(value.trim())
                && !"[]".equalsIgnoreCase(value.trim())
                ) {
            return false;
        }
        return true;
    }

    public static boolean isAllEmpty(View... views) {
        if (null == views || views.length == 0) {
            throw new IllegalArgumentException("value is an array,should not be empty or null");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            if (view instanceof TextView) {
                sb.append(((TextView) view).getText().toString().trim());
            }
            if (view instanceof EditText) {
                sb.append(((EditText) view).getText().toString().trim());
            }
        }
        return isEmpty(sb.toString());
    }

    /**
     * 是否所有值都为空
     */
    public static boolean isAllEmpty(String... value) {
        if (null == value || value.length == 0) {
            throw new IllegalArgumentException("value is an array,should not be empty or null");
        }
        boolean isEmpty = false;
        for (int i = 0; i < value.length; i++) {
            if (isEmpty(value[i])) {
                isEmpty = true;
                continue;
            }
            isEmpty = false;
            break;
        }
        return isEmpty;
    }

    /**
     * 判断值里面是否有一些是空的
     */
    public static boolean hasValueEmpty(String... value) {
        if (null == value || value.length == 0) {
            throw new IllegalArgumentException("value is an array,should not be empty or null");
        }
        boolean hasEmpty = false;
        for (int i = 0; i < value.length; i++) {
            if (isEmpty(value[i])) {
                hasEmpty = true;
                break;
            }
        }
        return hasEmpty;
    }

    //判断html内容是否body为空
    public static boolean isHtmlBodyEmpty(String html) {
        if (!isEmpty(html) &&
                html.lastIndexOf("<body") >= 0 &&
                html.lastIndexOf("</body>") > 0) {
            String body = html.substring(html.lastIndexOf("<body") + 6, html.lastIndexOf("</body>"));
            //LogUtils.d("StringUtils->isHtmlBodyEmpty:body=" + body);
            if (!isEmpty(body)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String formatDate2String(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static Date formatString2Date(String dateString, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return date;
        }
    }

    /**
     * @param date
     * @param format
     * @param isShowCurrentYear 若是当年的时间是否显示年份
     * @return
     */
    public static String formatDate2String(Date date, String format, boolean isShowCurrentYear) {

        if (isShowCurrentYear) { //显示
            return formatDate2String(date, format);
        } else {//不显示
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date());
            int currentYear = calendar.get(Calendar.YEAR);
            if (year == currentYear) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                return dateFormat.format(date);
            } else {
                return formatDate2String(date, format);
            }
        }
    }

    /**
     * @param dateString
     * @param parttern
     * @param isShowCurrentYear 若是当年的时间是否显示年份
     * @return
     */
    public static String formatDate2String(String dateString, String parttern, boolean isShowCurrentYear) {
        Date date = formatString2Date(dateString, parttern);
        if (isShowCurrentYear) { //显示
            return formatDate2String(date, parttern);
        } else {//不显示
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date());
            int currentYear = calendar.get(Calendar.YEAR);
            if (year == currentYear) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
                return dateFormat.format(date);
            } else {
                return formatDate2String(date, parttern);
            }
        }
    }

    /**
     * 自带JsonObject 解析json时防止特殊字符导致异常
     */
    public static String htmlSafeReplacementChars(String str) {
        return str.replaceAll("<", "\\u003c").replaceAll(">", "\\u003e");//.replaceAll("&","\\u0026").replaceAll("=","\\u003d").replaceAll("\\'","\\u0027");
    }

    /**
     * 计算中英文字符混合占中文长度
     *
     * @param value
     * @return
     */
    public static int lengthOfQuanJiao(String value) {
        if (value == null) return 0;
        StringBuffer buff = new StringBuffer(value);
        float length = 0.0f;
        String stmp;
        for (int i = 0; i < buff.length(); i++) {
            stmp = buff.substring(i, i + 1);
            try {
                stmp = new String(stmp.getBytes("utf-8"));
            } catch (Exception e) {
            }
            if (stmp.getBytes().length > 1) {
                length += 1;
            } else {
                length += 0.5f;
            }
        }
        return Math.round(length);
    }


    /**
     * null string to empty string
     * <p>
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     *
     * @param str
     * @return
     */
    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * capitalize first letter
     * <p>
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     *
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str
                : new StringBuilder(str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * get innerHtml from href
     * <p>
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     *
     * @param href
     * @return <ul>
     * <li>if href is null, return ""</li>
     * <li>if not match regx, return source</li>
     * <li>return the last string that match regx</li>
     * </ul>
     */
    public static String getHrefInnerHtml(String href) {
        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }

    /**
     * process special char in html
     * <p>
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     *
     * @param source
     * @return
     */
    public static String htmlEscapeCharsToString(String source) {
        return isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    /**
     * 替换字符串里面的  &gt; &lt; &amp; &quot; &#039 &nbsp;  <br>
     * 用于替换被服务端转义掉的字符(服务端不愿意处理只能客户端处理(-_-))
     *
     * @param content 内容
     * @return 去掉后的内容
     */
    public static String replaceSpecialchar(String content) {
        return TextUtils.isEmpty(content) ? content : content.replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replaceAll("&#039", "'")
                .replaceAll("&nbsp;", "\t")
                .replaceAll("&quot;", "\"")
                .replaceAll("&#x60;", "`")
                .replaceAll("&#x2F;", "/")
                .replaceAll("&#x27;", "\'")
                .replaceAll("&divide;", "÷")
                .replaceAll("&ldquo;", "“")
                .replaceAll("&rdquo;", "”")
                .replaceAll("&middot;", "·")
                .replaceAll("&mdash;", "—")
                .replaceAll("&circ;", "ˆ")
                .replaceAll("&tilde;", "")
                .replaceAll("&ensp;", " ")
                .replaceAll("&emsp;", " ")
                .replaceAll("&thinsp;", " ")
                .replaceAll("&zwnj;", " ")
                .replaceAll("&zwj;", " ")
                .replaceAll("&lrm;", " ")
                .replaceAll("&rlm;", " ")
                .replaceAll("&ndash;", "–")
                .replaceAll("&lsquo;", "‘")
                .replaceAll("&rsquo;", "’")
                .replaceAll("&sbquo;", "‚")
                .replaceAll("&bdquo;", "„")
                .replaceAll("&lsaquo;", "‹")
                .replaceAll("&rsaquo;", "›")
                .replaceAll("&#x2F;", "/")
                .replaceAll("&hellip;", "…")
                .replaceAll("&amp;", "&");
    }

    /**
     * 仅仅对几种特殊的html字符进行处理
     *
     * @param content
     * @return
     */
    public static String replaceFromHTMLToCode(String content) {
        return TextUtils.isEmpty(content) ? content : content
                .replaceAll("&#x60;", "`")
                .replaceAll("&#x2F;", "/")
                .replaceAll("&#x27;", "'")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .replaceAll("&lt;", "<")
                .replaceAll("&amp;", "&");
    }

    /**
     * transform half width char to full width char
     * <p>
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * transform full width char to half width char
     * <p>
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    public static String divisionByP(String sourceStr, int p, String regular) {
        if (TextUtils.isEmpty(sourceStr)) return "";
        if (p == 0 || p > sourceStr.length() || TextUtils.isEmpty(regular)) return sourceStr;
        String destStr = "";
        for (int i = 0; i < sourceStr.length(); i++) {
            if (i * p + p > sourceStr.length()) {
                destStr += sourceStr.substring(i * p, sourceStr.length());
                break;
            }
            destStr += sourceStr.substring(i * p, i * p + p) + regular;
        }
        if (destStr.endsWith(regular)) {
            destStr = destStr.substring(0, destStr.length() - 1);
        }
        return destStr;
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
//            if (c[i] == ' ') {
//                c[i] = '\u3000';                 //采用十六进制,相当于十进制的12288
//            } else
            if (c[i] < '\177') {    //采用八进制,相当于十进制的127
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {


        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);

        return returnString;
    }

    public static String getNoPointStr(String pointStr) {
        if (pointStr == null || pointStr.trim().equals("")) return "";
        return pointStr.replaceAll("\\.", "");
    }

    /**
     * 屏蔽第三方昵称中passport不支持的字符
     */
    public static String replaceIllegalChars(String str) {
        StringBuffer sb = new StringBuffer();
        if (!isEmpty(str)) {
            String[] strs = str.split("");
            for (int i = 0; i < strs.length; i++) {
                if (!strs[i].matches("([a-zA-Z0-9)_]|[^\\x00-\\xff])*") || strs[i].length() > 1) {
                    strs[i] = "";
                }
                if (getTextLengthInt(sb.toString() + strs[i]) > 20) {
                    break;
                } else {
                    sb.append(strs[i]);
                }
//                sb.append(strs[i]);
            }
        }
        String sTemp = sb.toString();
        if (sTemp == null) {
            sTemp = "";
        }
        return sTemp;
    }

    /**
     * 获取字符数量
     * 汉字占2个，英文占一个
     *
     * @param text
     * @return
     */
    public static int getTextLengthInt(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            int num = text.charAt(i);
            if (num > 0 && num < 127) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    /**
     * 1.3 设置符合条件的字符的颜色
     *
     * @param text  被匹配的文本
     * @param color 颜色
     * @param rep   正则表达式
     * @return
     */
    public static SpannableStringBuilder setColorSpan(String text, int color, String rep) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text.trim());
        Pattern p = Pattern.compile(rep);
        Matcher m = p.matcher(text);
        while (m.find()) {
            builder.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public static SpannableStringBuilder setSizeColorSpan(String text, int color, int size, String rep) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text.trim());
        Pattern p = Pattern.compile(rep);
        Matcher m = p.matcher(text);
        while (m.find()) {
            builder.setSpan(new AbsoluteSizeSpan(size), m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),  m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
        }
        return builder;
    }

    public static CharSequence setColorSpan(String text, int color) {
        SpannableString ss = new SpannableString(text.trim());
        ss.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 9999及以下显示实际数字，10000以上显示×.×万，999万以上显示999万+
     *
     * @param num 目标数字
     * @return 返回处理后的字符串
     */
    public static String formatNum(int num) {
        if (num >= 1E4 && num < 999 * 1E4) {
            double dt = (double) num / 1E4;
            DecimalFormat df = new DecimalFormat("0.0");
            return df.format(dt) + "万";
        } else if (num >= 999 * 1E4) {
            return "999万+";
        } else {
            return num + "";
        }
    }

    public static String maxEms(String str, int num) {
        if (!isEmpty(str) && num >= 0 && str.length() > num) {
            return str.substring(0, num) + "...";
        }
        return str;
    }

    /**
     * 过滤转义字符
     */
    public static String replaceEscapeCharacter(String str) {
        String[] escapes = new String[]{"\n", "\r","\t"};
        if (!StringUtils.isEmpty(str)) {
            for (String escape : escapes) {
                str = str.replaceAll(escape, "");
            }
        }
        return str.trim();
    }

    public static String formatMMSS(long duration) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(duration);
    }

    /**
     * 过滤emoj表情
     */
    public static final InputFilter getEmojiFilter(final Context ctx, final String msg) {
        return new InputFilter() {

            String str = "[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]";
            Pattern emoji = Pattern.compile(str, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    ToastUtils.showShort(ctx, msg);
                    return "";
                }
                return null;
            }
        };
    }
}
