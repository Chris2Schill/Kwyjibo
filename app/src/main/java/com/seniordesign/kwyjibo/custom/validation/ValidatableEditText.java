package com.seniordesign.kwyjibo.custom.validation;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

/*
 * This class is an extension of androids EditText providing automatic form validation based
 * on the 'android:inputType' attribute. If the inputType is not provided, the constructor will
 * throw an exception. Providing validation for different form types is accomplished by providing an
 * appropriate implementation of IFormValidator in setValidationType(int).
 */
public class ValidatableEditText extends EditText{

    private FormValidator validator;
    private boolean validated;
    private boolean hasHadFocus;

    private static final String exceptionString = "You must provide a valid " +
            "validationType for ValidatableEditText.";

    public ValidatableEditText(Context context) {
        super(context);
        init();
    }

    public ValidatableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ValidatableEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ValidatableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        this.validated = false;
        this.hasHadFocus = false;
        int inputType = this.getInputType();
        setValidationType(inputType);
    }

    private void setValidationType(int inputType){
        IFormValidation formValidationLogic;
        if (inputType != 0){
            switch (inputType) {
                case InputType.TYPE_CLASS_TEXT:
                    formValidationLogic = new UsernameLogic();
                    break;
                case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                    formValidationLogic = new EmailLogic();
                    break;
                case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD:
                    formValidationLogic = new PasswordLogic();
                    break;
                case InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE:
                    formValidationLogic = new ClipNameLogic();
                    break;
                default:
                    throw new IllegalStateException(exceptionString);
            }
        }else{
            throw new IllegalStateException(exceptionString);
        }
        validator = new FormValidator(this, formValidationLogic);
        this.addTextChangedListener(validator);
        this.setOnFocusChangeListener(validator.getFocusListener());
    }

    public void validate(){
        validator.getValidationLogic().validate(this);
        hasHadFocus = true;
    }

    public boolean getValidated(){
        return validated;
    }
    public void setValidated(boolean validated){
        this.validated = validated;
    }

    public void setValidator(FormValidator validator){
        this.validator = validator;
    }
    public FormValidator getValidator(){
        return validator;
    }

    public void setHasHadFocus(boolean hasHadFocus){
        this.hasHadFocus = hasHadFocus;
    }
    public boolean hasHadFocus(){
        return hasHadFocus;
    }

}
