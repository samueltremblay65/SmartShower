package com.example.smartshower;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class FormValidationHelper {
    Context context;

    public FormValidationHelper(Context context)
    {
        this.context = context;
    }
    public void showErrorDialog(String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public boolean validateEditTextInput_Text(EditText editText, String fieldName)
    {
        return validateEditTextInput_Text(editText, 64, fieldName);
    }

    public boolean validateEditTextInput_Text(TextInputEditText editText, int charLimit, String fieldName)
    {
        String value = editText.getText().toString().trim();
        if(value.equals(""))
        {
            // TODO: add error message element
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        if(value.length() > charLimit)
        {
            showErrorDialog("Error: input is too long");
            // TODO: add error message
            return false;
        }

        return true;
    }
    public boolean validateEditTextInput_Text(EditText editText, int charLimit, String fieldName)
    {
        String value = editText.getText().toString().trim();
        if(value.equals(""))
        {
            // TODO: add error message element
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        if(value.length() > charLimit)
        {
            showErrorDialog("Error: input is too long");
            // TODO: add error message
            return false;
        }

        return true;
    }

    public boolean validateEditTextInput_Email(EditText editText, String fieldName)
    {
        String value = editText.getText().toString().trim();
        if(value.equals(""))
        {
            // TODO: add error message element
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        if(value.length() > 128)
        {
            showErrorDialog("Your email address must contain less than 128 characters");
            return false;
        }

        // TODO: Email validation

        return true;
    }
    public boolean validateEditTextInput_Number(EditText editText, String fieldName)
    {
        String input = editText.getText().toString().trim();

        if(input.length() == 0)
        {
            // TODO: add error message
            showErrorDialog("Error: mandatory field is empty");
            return false;
        }

        try {
            Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            // TODO: add error message
            showErrorDialog("Error: could not read integer from input");
            return false;
        }
        return true;
    }

    public boolean validateEditTextInput_Number(EditText editText, int lowerBound, int upperBound, String fieldName)
    {
        String input = editText.getText().toString().trim();
        int value;

        if(input.length() == 0)
        {
            showErrorDialog(String.format("Please enter a value for the %s field", fieldName));
            return false;
        }

        try {
            value = Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            showErrorDialog("Error: could not read integer from input");
            return false;
        }

        if(!(value >= lowerBound && value <= upperBound))
        {
            showErrorDialog(String.format("%s must be between %d and %d", fieldName, lowerBound, upperBound));
        }

        return (value >= lowerBound && value <= upperBound);
    }

    // Should always be validated before call
    public int getIntegerFromEditText(EditText editText, String fieldName)
    {
        String input = editText.getText().toString().trim();
        return Integer.parseInt(input);
    }
}
