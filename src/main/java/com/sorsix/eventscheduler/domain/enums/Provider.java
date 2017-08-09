package com.sorsix.eventscheduler.domain.enums;

public enum Provider {

    LOCAL {
        @Override
        public String getLoginUrl() {
            return "/login";
        }
    },
    GITHUB {
        @Override
        public String getLoginUrl() {
            return "/api/public/login/github";
        }
    },
    GOOGLE {
        @Override
        public String getLoginUrl() {
            return "/api/public/login/google";
        }
    },
    FACEBOOK {
        @Override
        public String getLoginUrl() {
            return "/api/public/login/facebook";
        }
    };

    public String getLoginUrl() {
        return null;
    }
}
