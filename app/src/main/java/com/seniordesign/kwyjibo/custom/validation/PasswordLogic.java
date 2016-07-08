package com.seniordesign.kwyjibo.custom.validation;

import android.text.TextUtils;

public class PasswordLogic implements IFormValidation {
    @Override
    public void validate(ValidatableEditText editText) {
//        Drawable[] icons = editText.getCompoundDrawables();
//        Drawable left = icons[0];
//        Drawable top = icons[1];
//        Drawable right = icons[2];
//        Drawable bottom = icons[3];

        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)){
            editText.setError("Password field must not be empty.");
        }else if (s.length() > 0 && s.length() < 8){
            editText.setError("Password must be at least 8 characters.");
        }else{
//            Drawable validIcon = ApplicationWrapper.getDrawableFromId(android.R.drawable.checkbox_on_background);
            editText.setError(null);
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, validIcon, bottom);
        }
    }
}
