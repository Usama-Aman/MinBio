package com.vic.vicwsp.Utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.vic.vicwsp.R;

public class CustomTextWatcher {
    public CustomTextWatcher() {
    }

    ImageView imgTickError;
    ImageView imgTickError2;
    Context ctx;
    EditText etPassword;

    public CustomTextWatcher(ImageView imgTickError, Context ctx) {
        this.imgTickError = imgTickError;
        this.ctx = ctx;
    }

    public CustomTextWatcher(ImageView imgTickError, EditText password, Context ctx) {
        this.imgTickError = imgTickError;
        this.ctx = ctx;
        etPassword = password;
    }

    public CustomTextWatcher(ImageView imgTickError, ImageView imgTickError2, EditText password, Context ctx) {
        this.imgTickError = imgTickError;
        this.ctx = ctx;
        etPassword = password;
        this.imgTickError2 = imgTickError2;
    }

    public TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
            } else {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
            }
        }
    };

    public TextWatcher businesstextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                imgTickError.setBackground(null);
            } else {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
            }
        }
    };


    public TextWatcher passwordMatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty() || s.toString().length() < 6) {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
                if (s.toString().isEmpty()) {
                    imgTickError.setBackground(null);
                }
            } else {
                if (s.toString().equals(etPassword.getText().toString())) {
                    imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
                } else {
                    imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
                }
            }
        }
    };


    public TextWatcher password = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty() || s.toString().length() < 6) {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
                if (s.toString().isEmpty()) {
                    imgTickError.setBackground(null);
                }
            } else {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
                if (s.toString().equals(etPassword.getText().toString())) {
                    imgTickError2.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
                } else {
                    imgTickError2.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
                }
            }

        }
    };

    public TextWatcher emailValidatorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
            } else {
                if (Common.isValidEmailId(s.toString())) {
                    imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.tick));
                } else {
                    imgTickError.setBackground(ctx.getResources().getDrawable(R.drawable.cross));
                }
            }
        }
    };


}
