package com.ssd.appssd.adapter;

import com.ssd.appssd.objects.User;

import java.util.ArrayList;

public interface DialogListener {
    void onDialogChange(ArrayList<String> userSaved);
}
