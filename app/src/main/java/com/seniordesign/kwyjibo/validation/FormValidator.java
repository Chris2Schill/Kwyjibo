package com.seniordesign.kwyjibo.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class FormValidator implements TextWatcher {
    private ValidatableEditText editText;
    protected IFormValidation validationLogic;

    public FormValidator(ValidatableEditText editText, IFormValidation validationLogic){
        this.editText = editText;
        this.validationLogic = validationLogic;
    }

    protected View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus){
                validationLogic.validate(editText);
                editText.setHasHadFocus(true);
            }
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Do Nothing */ }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { /* Do Nothing */}
    @Override
    public void afterTextChanged(Editable s) {
        if (editText.hasHadFocus()){
            validationLogic.validate(editText);
        }
    }

    public View.OnFocusChangeListener getFocusListener(){
        return focusListener;
    }

    public IFormValidation getValidationLogic(){
        return validationLogic;
    }
}
