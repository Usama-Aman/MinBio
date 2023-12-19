package com.vic.vicwsp.Models.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("company_name")
        @Expose
        private String companyName;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("password_confirmation")
        @Expose
        private String passwordConfirmation;
        @SerializedName("siret_no")
        @Expose
        private String siret_no;
        @SerializedName("country_code")
        @Expose
        private String country_code;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("iso2")
        @Expose
        private String iso2;

    public RegisterRequest(String name, String email, String companyName, String password, String passwordConfirmation, String siret_no, String country_code, String phone, String iso2) {
        this.name = name;
        this.email = email;
        this.companyName = companyName;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.siret_no = siret_no;
        this.country_code = country_code;
        this.phone = phone;
        this.iso2 = iso2;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPasswordConfirmation() {
            return passwordConfirmation;
        }

        public void setPasswordConfirmation(String passwordConfirmation) {
            this.passwordConfirmation = passwordConfirmation;
        }

        public String getSiret_no() {
            return siret_no;
        }

        public void setSiret_no(String siret_no) {
            this.siret_no = siret_no;
        }


}
