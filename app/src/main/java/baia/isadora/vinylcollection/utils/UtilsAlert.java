package baia.isadora.vinylcollection.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import baia.isadora.vinylcollection.R;

public final class UtilsAlert {
    private UtilsAlert(){

    }
    public static void showWarning(Context context, int messageId){
        showWarning(context, context.getString(messageId), null);
    }

    public static void showWarning(Context context, int messageId, DialogInterface.OnClickListener listener){
        showWarning(context, context.getString(messageId), listener);
    }

    public static void showWarning(Context context, String message, android.content.DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(message);

        builder.setNeutralButton(R.string.ok, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void confirmAction(Context context, int messageId, DialogInterface.OnClickListener listenerYes, DialogInterface.OnClickListener listenerNo){
        confirmAction(context, context.getString(messageId), listenerYes, listenerNo);
    }

    public static void confirmAction(Context context, String message, DialogInterface.OnClickListener listenerYes, DialogInterface.OnClickListener listenerNo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.confirmation);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.yes, listenerYes);
        builder.setNegativeButton(R.string.no, listenerNo);

        AlertDialog alert = builder.create();
        alert.show();
    }

}
