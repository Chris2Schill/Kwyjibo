package com.seniordesign.kwyjibo.custom.validation;

import android.text.TextUtils;

public class EmailLogic implements IFormValidation {
    @Override
    public void validate(ValidatableEditText editText) {
//        Drawable[] icons = editText.getCompoundDrawables();
//        Drawable left = icons[0];
//        Drawable top = icons[1];
//        Drawable right = icons[2];
//        Drawable bottom = icons[3];

        String s = editText.getText().toString();
        if (TextUtils.isEmpty(s)){
            editText.setError("Email field must not be empty.");
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, null, bottom);
        }else if (!isValidEmail(s)){
            editText.setError("Not a proper email format.");
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, null, bottom);
        }else{
//            Drawable validIcon = ApplicationWrapper.getDrawableFromId(android.R.drawable.checkbox_on_background);
            editText.setError(null);
//            editText.setCompoundDrawablesWithIntrinsicBounds(left, top, validIcon, bottom);
        }
    }

    private static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
