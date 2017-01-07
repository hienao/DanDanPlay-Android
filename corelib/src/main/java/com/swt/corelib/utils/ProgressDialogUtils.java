package com.swt.corelib.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.TextView;

import com.swt.corelib.R;


/**
 * 阻塞屏幕
 */
public class ProgressDialogUtils {
	private static Dialog dlg = null;
	private static onProcessDialogListener oLsner = null;

	public interface onProcessDialogListener {
		void onCancelled();
	}

	public static void setMessage(String strMsg) {
		if (dlg != null) {
			((TextView) dlg.findViewById(R.id.loading_msg)).setText(strMsg);
		}
	}

	public static void showDialog(Context ct, String strMsg, onProcessDialogListener lsn) {
		try {
			if (ct != null) {
				if (dlg == null) {
					dlg = new Dialog(ct, R.style.my_dialog);
					dlg.setContentView(R.layout.dialog_progressbar);
					dlg.setCancelable(true);
					dlg.setCanceledOnTouchOutside(false);
//					dlg.setMessage(strMsg);
					((TextView) dlg.findViewById(R.id.loading_msg)).setText(strMsg);
					oLsner = lsn;

					dlg.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							if (oLsner != null) {
								oLsner.onCancelled();
							}
							dismissDialog();
						}
					});

					dlg.show();
				} else {
//					dlg.setMessage(strMsg);
					((TextView) dlg.findViewById(R.id.loading_msg)).setText(strMsg);
				}
			}
		} catch (Exception e) {
		}
	}

	public static void showDialog(Context ct, String strMsg) {
		try {
			if (ct != null) {
				if (dlg == null) {
					dlg = new Dialog(ct, R.style.my_dialog);
					dlg.setContentView(R.layout.dialog_progressbar);
					dlg.setCancelable(false);
					dlg.setCanceledOnTouchOutside(false);
					((TextView) dlg.findViewById(R.id.loading_msg)).setText(strMsg);
					dlg.show();
				} else {
					((TextView) dlg.findViewById(R.id.loading_msg)).setText(strMsg);
				}
			}
		} catch (Exception e) {
		}
	}

	public static void dismissDialog() {
		try {
			if (dlg != null) {
				dlg.dismiss();
				dlg = null;
			}
		} catch (Exception e) {
		}
	}
}
