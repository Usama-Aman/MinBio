package com.vic.vicwsp.Models.Response.Complaint.ComplaintList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ComplaintTypeModel {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("type_fr")
        @Expose
        private String typeFr;

        public ComplaintTypeModel(Integer id, String type, String typeFr) {
            super();
            this.id = id;
            this.type = type;
            this.typeFr = typeFr;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTypeFr() {
            return typeFr;
        }

        public void setTypeFr(String typeFr) {
            this.typeFr = typeFr;
        }

}
