package com.seniordesign.kwyjibo.validation;

import android.text.TextUtils;

public class UsernameLogic implements IFormValidation {
    @Override
    public void validate(ValidatableEditText editText) {
//        Drawable[] icons = editText.getCompoundDrawables();
//        Drawable left = icons[0];
//        Drawable top = icons[1];
//        Drawable right = icons[2];
//        Drawable bottom = icons[3];

        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)){
            editText.setError("Field Cannot Be Empty.");
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, null, bottom);
        }else if (s.length() == 1){
            editText.setError("Username must be at least 2 characters long.");
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, null, bottom);
        }else{
//            Drawable validIcon = ApplicationWrapper.getDrawableFromId(android.R.drawable.checkbox_on_background);
            editText.setError(null);
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, validIcon, bottom);
        }
    }
}
