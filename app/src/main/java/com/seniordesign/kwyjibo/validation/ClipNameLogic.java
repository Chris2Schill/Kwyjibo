package com.seniordesign.kwyjibo.validation;

import android.text.TextUtils;

public class ClipNameLogic implements IFormValidation{
    @Override
    public void validate(ValidatableEditText editText) {
        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)){
            editText.setError("Sound clip must have a name.");
        }else{
            editText.setError(null);
        }
    }
}
