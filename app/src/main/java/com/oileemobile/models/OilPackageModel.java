package com.oileemobile.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created and coded by Shubham Mathur (OwnageByte) 2019-10-16 23:57
 **/
public class OilPackageModel implements Serializable {
    /**
     * package_category : 5w-30
     * packages : [{"id":1,"oil_type":"Synthetic","price":"50.00"},{"id":4,"oil_type":"Synthetic","price":"65.00"}]
     */

    private String package_category;

    private List<PackagesBean> packages;

    public String getPackage_category() {
        return package_category;
    }

    public List<PackagesBean> getPackages() {
        return packages;
    }

    public static class PackagesBean implements Serializable {
        /**
         * id : 1
         * oil_type : Synthetic
         * price : 50.00
         */

        private int id;
        private String oil_type;
        private String price;
        private boolean isSelected = false;

        public int getId() {
            return id;
        }

        public String getOil_type() {
            return oil_type;
        }

        public String getPrice() {
            return price;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
