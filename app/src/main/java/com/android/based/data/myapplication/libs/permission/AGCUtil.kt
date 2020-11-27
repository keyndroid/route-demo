package com.android.based.data.myapplication.libs.permission;

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.android.based.data.myapplication.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object AGCUtil {
    private val CALL_STACK_INDEX = 1
    private val ANONYMOUS_CLASS =
        Pattern.compile("(\\$\\d+)+$")
    private val MAX_TAG_LENGTH = 23
    private var mTag:String? = null

    fun tag(tag:String): AGCUtil {
        this.mTag=tag
        return AGCUtil
    }
//    fun e(message: String){
//        if (!TextUtils.isEmpty(message) && BuildConfig.DEBUG){
//            // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
//// because Robolectric runs them on the JVM but on Android the elements are different.
//            val stackTrace =
//                Throwable().stackTrace
//            check(stackTrace.size > CALL_STACK_INDEX) { "Synthetic stacktrace didn't have enough elements: are you using proguard?" }
//            var tag=   mTag
//            if (TextUtils.isEmpty(mTag)){
//                tag=   createStackElementTag(stackTrace[CALL_STACK_INDEX])
//            }
//            Log.e(tag, message)
//        }
//
//        mTag=null
//    }
    fun createStackElementTag(element: StackTraceElement): String? {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1)
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else tag.substring(0, MAX_TAG_LENGTH)
    }



    fun showToast(context: Context, msg: String?) {
        var updatedMessage = msg
        if (TextUtils.isEmpty(msg)){
            updatedMessage=context.getString(R.string.err_something_wrong)
        }
        Toast.makeText(context, updatedMessage, Toast.LENGTH_LONG).show()
    }



    fun getDateTimeStamp(format: String, date: String): Long {
        var timeStamp: Long = 0
        val locale = Locale("en")
        Locale.setDefault(locale)
        val formatter = SimpleDateFormat(format, locale)
        var mDate: Date? = null
        try {
            mDate = formatter.parse(date)
            timeStamp = mDate!!.time
        } catch (e: ParseException) {
            timeStamp = 0
            e.printStackTrace()
        }

        return timeStamp
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun startInstalledAppDetailsActivity(context: Context?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    fun isRTL(context: Context): Boolean {
        val configuration = context.resources.configuration
        return if (configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            true
        } else {
            false
        }
    }

    fun isRTL(locale: Locale): Boolean {
        val directionality = Character.getDirectionality(locale.displayName[0]).toInt()
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
    }

    fun setViewBasedOnRtl(v: View, context: Context) {
        val l = getCurrentLocale(context)
        if (l.language.equals("ar", ignoreCase = true)) {
            v.scaleY = -1.0f
            v.rotation = 180f
        } else {
            v.scaleY = 0f
            v.rotation = 0f
        }

    }

    fun setNotificationViewBasedOnRtl(v: View, context: Context) {
        val l = getCurrentLocale(context)
        if (l.language.equals("ar", ignoreCase = true)) {
            v.rotation = 90.0f
        } else {
            v.rotation = 270.0f
        }

    }

    fun getCurrentLocale(context: Context): Locale {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {

            context.resources.configuration.locale
        }


    }


    fun getDigitsString(digit: Int): String {
        return if (digit > 9) {
            "" + digit
        } else {
            "0$digit"
        }

    }

    fun getYearFromDate(birthdate: String): Int {
        val cal = Calendar.getInstance()
        val time = AGCUtil.getDateTimeStamp("yyyy-MM-dd", birthdate)
        cal.timeInMillis = time
        val dobYear = cal.get(Calendar.YEAR)
        val curYear = Calendar.getInstance().get(Calendar.YEAR)
        var diff = curYear - dobYear
        if (diff < 0) {
            diff = 0
        }
        return diff
    }

    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun setSpinnersDropDownHeight(context: Context, spinner: Spinner, listSize: Int) {
        if (listSize > 4) {
            try {
                val popup = Spinner::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true

                // Get private mPopup member variable and try cast to ListPopupWindow
                val popupWindow = popup.get(spinner) as android.widget.ListPopupWindow

                popupWindow.height = AGCUtil.convertDpToPixel(150f, context).toInt()

            } catch (e: NoClassDefFoundError) {
                // silently fail...
            } catch (e: ClassCastException) {
            } catch (e: NoSuchFieldException) {
            } catch (e: IllegalAccessException) {
            }

        }
    }

    fun getRating(amount: Float): String {
        return String.format("%.1f", amount)
    }

    fun getCurrencyFill(price: String): Spanned {
        //Sample font string here
        //String text = "<font color=#00dc99>First Color</font> <font color=#ffcc00>Second Color</font>";
        val fillString = StringBuilder()
        val maxCount = 3

        var minPrice = 0f
        try {
            minPrice = java.lang.Float.parseFloat(price)
        } catch (e: NullPointerException) {
            minPrice = 0f
        } catch (e: NumberFormatException) {
            minPrice = 0f
        }

        val fillColor: Int

        if (minPrice < 10) {
            fillColor = 1
        } else if (minPrice < 100) {
            fillColor = 2
        } else {
            fillColor = 3
        }

        for (i in 0 until maxCount) {
            if (i < fillColor) {
                fillString.append("<font color=#00dc99>")
            } else {
                fillString.append("<font color=#aaaaaa>")
            }

            fillString.append("$")
            if (i - 1 != maxCount) {
                fillString.append(" ")
            }
            fillString.append("</font> ")
        }
        return Html.fromHtml(fillString.toString())
    }


    fun getHtmlText( unformatedText:String?): Spanned {
        if (TextUtils.isEmpty(unformatedText)){
            return HtmlCompat.fromHtml("", HtmlCompat.FROM_HTML_MODE_LEGACY);
        }
        return HtmlCompat.fromHtml(unformatedText!!, HtmlCompat.FROM_HTML_MODE_LEGACY);

    }
//    fun logger(msg: String) {
//        if (!BuildConfig.FLAVOR.contains("live") && BuildConfig.DEBUG) {
//            Log.e("MyLogger", msg)
//        }
//    }


}
