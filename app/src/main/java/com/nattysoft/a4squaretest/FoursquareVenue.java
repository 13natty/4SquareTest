package com.nattysoft.a4squaretest;

/**
 * Created by F3838284 on 12/9/2016.
 */

public class FoursquareVenue {
    private String ID;
    private String name;
        private String city;

        private String category;

        public FoursquareVenue() {
            this.name = "";
            this.city = "";
            this.setCategory("");
        }

        public String getCity() {
            if (city.length() > 0) {
                return city;
            }
            return city;
        }

        public void setCity(String city) {
            if (city != null) {
                this.city = city.replaceAll("\\(", "").replaceAll("\\)", "");
            }
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getID() {
            return ID;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
}
